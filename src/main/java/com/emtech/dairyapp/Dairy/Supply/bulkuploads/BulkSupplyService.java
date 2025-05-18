package com.emtech.dairyapp.Dairy.Supply.bulkuploads;

import com.emtech.dairyapp.Auth.User.User;
import com.emtech.dairyapp.Auth.User.UserRepository;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocationsRepo;
import com.emtech.dairyapp.Configurations.ProductPriceConfiguration.ProductConfig;
import com.emtech.dairyapp.Configurations.ProductPriceConfiguration.ProductConfigRepo;
import com.emtech.dairyapp.Configurations.Routes.Route;
import com.emtech.dairyapp.Configurations.Routes.RouteRepo;
import com.emtech.dairyapp.Configurations.Utils.Formatter;
import com.emtech.dairyapp.Dairy.Supply.Codenerator;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
import com.emtech.dairyapp.Dairy.Supply.MilkCollections;
import com.emtech.dairyapp.Notifications.SMS.smsv2.SmsReqDto;
import com.emtech.dairyapp.Notifications.SMS.smsv2.SmsServiceV2;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@AllArgsConstructor
public class BulkSupplyService {

    @Autowired
    private final FarmerRepo farmerRepo;

    @Autowired
    private final ProductConfigRepo productConfigRepo;

    @Autowired
    private final RouteRepo routeRepo;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final MilkCollectionRepo milkCollectionRepo;

    @Autowired
    private final BulkDeliveryRepo bulkDeliveryRepo;

    @Autowired
    private final PickUpLocationsRepo pickUpLocationsRepo;

    @Autowired
    private final Codenerator codenerator;

    @Autowired
    private final SmsServiceV2 smsServiceV2;


    public Mono<EntityResponse<?>> uploadBulkDeliveries(FilePart filePart, String postedBy, String mobile) {
        EntityResponse<List<Object>> response = new EntityResponse<>();
        List<Object> failed = new ArrayList<>();
        List<BulkDelivery> bulkDeliveries = new ArrayList<>();
        SmsReqDto reqDto = new SmsReqDto();
        reqDto.setBulk(false);
         AtomicInteger success = new AtomicInteger();
         AtomicInteger failures = new AtomicInteger();
         AtomicReference<String> farmer = new AtomicReference<>();
         AtomicReference<String> route = new AtomicReference<>();


        return filePart.content()
                .collectList()
                .flatMap(dataBuffers -> {
                    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        dataBuffers.forEach(dataBuffer -> {
                            byte[] buffer = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(buffer);
                            outputStream.write(buffer, 0, buffer.length);
                        });

                        try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                            var fileUpload = getData(inputStream, filePart.filename());
                            List<BulkDto> bulkDtos = fileUpload.getEntity();

                            if (fileUpload.getStatusCode() != 200) {
                                response.setMessage(fileUpload.getMessage());
                                response.setStatusCode(fileUpload.getStatusCode());
                                return Mono.just(response);
                            }

                            for (BulkDto row : bulkDtos) {
                                MilkCollections milkSupply = new MilkCollections();
                                Optional<FarmerInfo> optionalFarmer = farmerRepo.findByFarmerNo(row.getFarmerNo());

                                log.info("checking farmer existence ---------- for {} ", row.getFarmerNo());
                                if (optionalFarmer.isEmpty()) {
                                    // create a response for failed step
                                    BulkDelivery bulkDelivery = getBulkDelivery(row, "farmer not found", postedBy, farmer.get(), route.get());
                                    failures.getAndIncrement();

                                    bulkDeliveries.add(bulkDelivery);
                                    log.info("farmer with member number {} not found", row.getFarmerNo());
                                    continue;
                                }
                                FarmerInfo farmerInfo = optionalFarmer.get();

                                //check if the record already exists before proceeding
                                log.info("checking if record was already saved....... for {}, {}, {}", row.getFarmerNo(), row.getDate(), row.getSession());
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                String formatted = formatter.format(row.getDate());

                                Integer duplicate = milkCollectionRepo.checkDuplicateEntry(row.getFarmerNo(), row.getSession(), formatted);

                                // getting farmer route
                                Optional<Route> optionalRoute = routeRepo.findById(farmerInfo.getRouteId());
                                if (optionalRoute.isEmpty()) {
                                    log.info("farmer route absent");
                                    continue;
                                }

                                farmer.set(farmerInfo.getName() + " " + farmerInfo.getLast_name());
                                route.set(optionalRoute.get().getRoute());

                                if (duplicate > 0) {
                                    // create a response for failed step
                                    BulkDelivery bulkDelivery = getBulkDelivery(row, "duplicate entry detected", postedBy, farmer.get(), route.get());
                                    failures.getAndIncrement();

                                    bulkDeliveries.add(bulkDelivery);
                                    log.info("duplicate entry detected .........");
                                    continue;
                                }

                                log.info("checking milk buying price for route ------ for {} ", farmerInfo.getRouteId() );
                                Optional<ProductConfig> configOptional = productConfigRepo.findByRouteFk(farmerInfo.getRouteId());
                                if (configOptional.isEmpty()) {
                                    // create a response for failed step
                                    BulkDelivery bulkDelivery = getBulkDelivery(row, "price config not found", postedBy, farmer.get(), route.get());
                                    failures.getAndIncrement();

                                    bulkDeliveries.add(bulkDelivery);
                                    log.info("Product config for route with id {} not found", farmerInfo.getRouteId());
                                    continue;
                                }

                                log.info("getting route collector ------ for {}", farmerInfo.getRouteId());
                                String collector = routeRepo.getFarmerCollector(farmerInfo.getRouteId());

                                if (collector.isEmpty()) {
                                    // create a response for failed step
                                    BulkDelivery bulkDelivery = getBulkDelivery(row, "collector not found", postedBy, farmer.get(), route.get());
                                    failures.getAndIncrement();

                                    bulkDeliveries.add(bulkDelivery);
                                    log.info("Collector not found for route {}", farmerInfo.getRouteId());
                                    continue;
                                }

                                Optional<User> optional = userRepository.findByUsername(collector);

                                if (optional.isEmpty()) {
                                    // create a response for failed step
                                    BulkDelivery bulkDelivery = getBulkDelivery(row, "collector data not found", postedBy, farmer.get(), route.get());
                                    failures.getAndIncrement();

                                    bulkDeliveries.add(bulkDelivery);
                                    log.info("Collector userdata not found for route {}", farmerInfo.getRouteId());
                                    continue;
                                }

                                BulkDelivery bulkDelivery = getBulkDelivery(row, "Success", postedBy, farmer.get(), route.get());
                                //set farmer route and name
                                bulkDelivery.setFarmer(farmerInfo.getName()+" "+farmerInfo.getLast_name());
                                bulkDelivery.setRoute(optionalRoute.get().getRoute());


                                bulkDeliveries.add(bulkDelivery);
                                User user = optional.get();

                                //set milk collection parameters
                                milkSupply.setFarmerNo(farmerInfo.getFarmer_no());
                                milkSupply.setOriginalQuantity(row.getQuantity());
                                milkSupply.setQuantity(row.getQuantity());
                                milkSupply.setCollectionDate(row.getDate());
                                milkSupply.setCollectionNumber(codenerator.codeGenerator(farmerInfo.getFarmer_no()));
                                milkSupply.setCollectorId(user.getId());
                                milkSupply.setProductType("Fresh Milk");
                                milkSupply.setCurrentPrice(configOptional.get().getBuyingPrice());
                                milkSupply.setDeductedWeight(0.0);
                                milkSupply.setEvent("Collection");
                                milkSupply.setPaymentStatus('N');
                                milkSupply.setSession("Session 1");
                                milkSupply.setUpdatedStatus('N');
                                milkSupply.setRouteFk(farmerInfo.getRouteId());
                                milkSupply.setReturned('N');
                                milkSupply.setAmount(row.getQuantity() * configOptional.get().getBuyingPrice());

                                success.getAndIncrement();
                                milkCollectionRepo.save(milkSupply);

                                // get month and year
                                SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
                                SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
                                int monthNo = Integer.parseInt(formatMonth.format(row.getDate()));
                                String year = formatYear.format(row.getDate());

                                //retrieve updated monthly total
                                Double monthTotal = milkCollectionRepo.getMonthyAccumulation(farmerInfo.getFarmer_no(), monthNo, year);
                                String session = row.getSession().equalsIgnoreCase("Session 1") ? "Morning" : (row.getSession().equalsIgnoreCase("Session 2") ? "Afternoon" : "Evening");

                                log.info("new month total for {} , farmer no {}, month {} , updated qty: {} .......", farmerInfo.getName(), farmerInfo.getFarmer_no(), monthNo, monthTotal);
                                //send sms if farmer phone no exists
                                String message = "Dear "+farmerInfo.getName()+", farmer no "+farmerInfo.getFarmer_no()+", delivery of " +
                                        row.getQuantity()+" kgs, "+session+" Session for "+ Formatter.formatDate(row.getDate()) +
                                        " received"+" Monthly Total: "+monthTotal+" kgs";

                                if (farmerInfo.getMobile_no() != null) {
                                    log.info("sending sms .......to {} .....farmer number {}", farmerInfo.getName(), farmerInfo.getFarmer_no());
                                    reqDto.setMessage(message);
                                    reqDto.setPhoneNumber(Formatter.formatPhone(farmerInfo.getMobile_no()));
                                    smsServiceV2.SMSNotification(reqDto);
                                }
                            }

                            //notify staff member on status of delivery uploads
                            String message = "Hello "+postedBy+", successful uploads: "+success+", failed uploads: "+failures+". on "+Formatter.formatDate(new Date());
                            reqDto.setMessage(message);

                            reqDto.setPhoneNumber(Formatter.formatPhone("0112209296"));
                            smsServiceV2.SMSNotification(reqDto);

                            reqDto.setPhoneNumber(Formatter.formatPhone("0715318204"));
                            smsServiceV2.SMSNotification(reqDto);

                            if (mobile != null && !mobile.equalsIgnoreCase("0715318204")) {
                                reqDto.setPhoneNumber(Formatter.formatPhone(mobile));
                                smsServiceV2.SMSNotification(reqDto);
                            }


                            bulkDeliveryRepo.saveAll(bulkDeliveries);
                            response.setMessage("Bulk Collections uploaded successfully");
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(failed);
                        } catch (Exception e) {
                            log.error(e.toString());
                            response.setMessage("Bad Request");
                            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        }
                    } catch (IOException e) {
                        log.error(e.toString());
                        response.setMessage("Error reading file");
                        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    }
                    return Mono.just(response);
                });
    }

    public EntityResponse<?> getUploadsByDateRange(String from, String to) {
        EntityResponse<List<BulkDelivery>> response = new EntityResponse<>();

        try {
            List<BulkDelivery> bulkDeliveries = bulkDeliveryRepo.getUploadsByDate(from, to);

            response.setMessage("found "+bulkDeliveries.size()+" bulk uploads");
            response.setEntity(bulkDeliveries);
            response.setStatusCode(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("Unable to get records");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    private static BulkDelivery getBulkDelivery(BulkDto row, String message, String postedBy, String farmer, String route) {
        BulkDelivery bulkDelivery = new BulkDelivery();
        bulkDelivery.setFarmerNo(row.getFarmerNo());
        bulkDelivery.setQuantity(row.getQuantity());
        bulkDelivery.setDate(row.getDate());
        bulkDelivery.setSession(row.getSession());
        bulkDelivery.setReason(message);
        bulkDelivery.setPostedBy(postedBy);
        bulkDelivery.setFarmer(farmer);
        bulkDelivery.setRoute(route);
        return bulkDelivery;
    }

    public EntityResponse<List<BulkDto>> getData(InputStream inputStream, String filename) {
        List<BulkDto> bulkDtos = new LinkedList<>();
        EntityResponse<List<BulkDto>> response = new EntityResponse<>();

        try {
            Workbook workbook = null;
            if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage("Kindly upload an excel file");
                return response;
            }

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            //check that the correct header rows are being passed
            if (!validHeaders(headerRow)) {
                response.setMessage("Wrong header rows passed");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return response;
            }


            for (Row record : sheet) {
                if (record.getRowNum() == 0) {
                    continue;
                }

                BulkDto bulkDto = new BulkDto();

                if (record.getCell(0) != null) {
                    var farmerNo = record.getCell(0).getNumericCellValue();
                    bulkDto.setFarmerNo((int) farmerNo);
                } else {
                    continue;
                }

                if (record.getCell(1) != null) {
                    Double quantity = record.getCell(1).getNumericCellValue();
                    bulkDto.setQuantity(quantity);
                } else {
                    continue;
                }

                if (record.getCell(2) != null) {
                    Date date = record.getCell(2).getDateCellValue();
                    bulkDto.setDate(date);
                } else {
                    continue;
                }

                if (record.getCell(3) != null) {
                    String session = record.getCell(3).getStringCellValue();
                    bulkDto.setSession(session);
                } else {
                    continue;
                }

                bulkDtos.add(bulkDto);
            }

            if (bulkDtos.isEmpty()) {
                response.setMessage("Data not found");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return response;
            }

            response.setMessage("success");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(bulkDtos);
        } catch (IOException e) {
            log.error(e.toString());
            response.setMessage("Incorrect data format");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }


    private boolean validHeaders(Row headerRow) {
        if (headerRow == null) {
            return false;
        }

        String[] expectedHeaders = {"farmer", "quantity", "date", "session"};

        for (int i=0; i<expectedHeaders.length; i++ ) {
            if (headerRow.getCell(i) == null || !expectedHeaders[i].equalsIgnoreCase(headerRow.getCell(i).getStringCellValue())) {
                return false;
            }
        }
        return true;
    }
}
