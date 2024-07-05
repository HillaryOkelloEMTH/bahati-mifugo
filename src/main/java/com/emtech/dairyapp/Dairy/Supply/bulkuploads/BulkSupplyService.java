package com.emtech.dairyapp.Dairy.Supply.bulkuploads;

import com.emtech.dairyapp.Auth.User.User;
import com.emtech.dairyapp.Auth.User.UserRepository;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.ProductPriceConfiguration.ProductConfig;
import com.emtech.dairyapp.Configurations.ProductPriceConfiguration.ProductConfigRepo;
import com.emtech.dairyapp.Configurations.Routes.RouteRepo;
import com.emtech.dairyapp.Dairy.Interface.RouteData;
import com.emtech.dairyapp.Dairy.Supply.Codenerator;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
import com.emtech.dairyapp.Dairy.Supply.MilkCollections;
import com.emtech.dairyapp.Dairy.Supply.MilkSupply;
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
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
    private final Codenerator codenerator;


    public Mono<EntityResponse<?>> uploadBulkDeliveries(FilePart filePart) {
        EntityResponse<?> response = new EntityResponse<>();
        List<MilkCollections> milkCollections = new LinkedList<>();

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

                            System.out.println("received data"+bulkDtos);

//                            for (BulkDto row : bulkDtos) {
//                                MilkCollections milkSupply = new MilkCollections();
//                                Optional<FarmerInfo> optionalFarmer = farmerRepo.findByFarmerNo(row.getFarmerNo());
//
//                                log.info("checking farmer existence ----------");
//                                if (optionalFarmer.isEmpty()) {
//                                    log.info("farmer with member number {} not found", row.getFarmerNo());
//                                    response.setMessage("Farmer with number " + row.getFarmerNo() + " not found");
//                                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                                    continue;
//                                }
//                                FarmerInfo farmerInfo = optionalFarmer.get();
//
//                                log.info("checking milk buying price for route ------");
//                                Optional<ProductConfig> configOptional = productConfigRepo.findByRouteFk(farmerInfo.getRouteId());
//                                if (configOptional.isEmpty()) {
//                                    log.info("Product config for route with id {} not found", farmerInfo.getRouteId());
//                                    response.setMessage("Product config for route not found");
//                                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                                    continue;
//                                }
//
//                                log.info("getting route collector ------");
//                                String collector = routeRepo.getFarmerCollector(farmerInfo.getRouteId());
//
//                                if (collector.isEmpty()) {
//                                    log.info("Collector not found for route {}", farmerInfo.getRouteId());
//                                    response.setMessage("Collector not found for route");
//                                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                                    continue;
//                                }
//
//                                Optional<User> optional = userRepository.findByUsername(collector);
//
//                                if (optional.isEmpty()) {
//                                    log.info("Collector userdata not found for route {}", farmerInfo.getRouteId());
//                                    response.setMessage("Collector userdata not found for route");
//                                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                                    continue;
//                                }
//
//                                User user = optional.get();
//
//                                milkSupply.setFarmerNo(farmerInfo.getFarmer_no());
//                                milkSupply.setOriginalQuantity(row.getQuantity());
//                                milkSupply.setQuantity(row.getQuantity());
//                                milkSupply.setCollectionDate(row.getDate());
//                                milkSupply.setCollectionNumber(codenerator.codeGenerator(farmerInfo.getFarmer_no()));
//                                milkSupply.setCollectorId(user.getId());
//                                milkSupply.setCurrentPrice(configOptional.get().getBuyingPrice());
//                                milkSupply.setDeductedWeight(0.0);
//                                milkSupply.setEvent("Collection");
//                                milkSupply.setPaymentStatus('N');
//                                milkSupply.setSession("Session 1");
//                                milkSupply.setUpdatedStatus('N');
//                                milkSupply.setRouteFk(farmerInfo.getRouteId());
//                                milkSupply.setReturned('N');
//                                milkSupply.setAmount(row.getQuantity() * configOptional.get().getBuyingPrice());
//
//                                milkCollections.add(milkSupply);
//                            }
//
//                            milkCollectionRepo.saveAll(milkCollections);

                            response.setMessage("Collection uploaded successfully");
                            response.setStatusCode(HttpStatus.OK.value());
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
                }

                bulkDtos.add(bulkDto);
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
}
