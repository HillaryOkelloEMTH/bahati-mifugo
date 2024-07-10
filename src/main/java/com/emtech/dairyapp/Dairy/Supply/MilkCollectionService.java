package com.emtech.dairyapp.Dairy.Supply;

import com.emtech.dairyapp.Analytics.AnalyticsData;
import com.emtech.dairyapp.Configurations.CanManagement.Can;
import com.emtech.dairyapp.Configurations.CanManagement.CanRepo;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.ProductPriceConfiguration.ProductConfig;
import com.emtech.dairyapp.Configurations.ProductPriceConfiguration.ProductConfigRepo;
import com.emtech.dairyapp.Configurations.Routes.Route;
import com.emtech.dairyapp.Configurations.Routes.RouteRepo;
import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import com.emtech.dairyapp.Dairy.FloatTracking.FloatManager;
import com.emtech.dairyapp.Dairy.FloatTracking.FloatManagerRepo;
import com.emtech.dairyapp.Dairy.Interface.*;
import com.emtech.dairyapp.Notifications.SMS.smsv2.SmsServiceV2;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static com.emtech.dairyapp.Configurations.Utils.Formatter.formatDate;
import static com.emtech.dairyapp.Configurations.Utils.Formatter.formatPhone;

@Service
@Slf4j
public class MilkCollectionService {


    private final MilkCollectionRepo milkCollectionRepo;
    private final ProductConfigRepo productConfigRepo;
    private final FloatManagerRepo floatManagerRepo;
    private final Codenerator codenerator;
    private final SmsServiceV2 smsServiceV2;

    private final FarmerRepo farmerRepo;
    private final CanRepo canRepo;
    private final RouteRepo routeRepo;

    @Value("${sms.enable}")
    private boolean sms;


    public MilkCollectionService(MilkCollectionRepo milkCollectionRepo, ProductConfigRepo productConfigRepo, FloatManagerRepo floatManagerRepo, Codenerator codenerator, SmsServiceV2 smsServiceV2, FarmerRepo farmerRepo, CanRepo canRepo, RouteRepo routeRepo) {
        this.milkCollectionRepo = milkCollectionRepo;
        this.productConfigRepo = productConfigRepo;
        this.floatManagerRepo = floatManagerRepo;
        this.codenerator = codenerator;
        this.smsServiceV2 = smsServiceV2;
        this.farmerRepo = farmerRepo;
        this.canRepo = canRepo;
        this.routeRepo = routeRepo;
    }


    public EntityResponse newcollection(MilkCollections collections) {

        EntityResponse response = new EntityResponse();
        try {


            String collectionNumber = codenerator.codeGenerator(collections.getFarmerNo());
            collections.setCollectionNumber(collectionNumber);


            collections.setProductType("Fresh Milk");
            String event = collections.getEvent();

            log.info("Price management fro route found...");
            if (event.equalsIgnoreCase("Buying")) {
                log.info("buying event");
//                collections.setQuantity(collections.getOriginalQuantity());
//                Double buyingPrice = collections.getCurrentPrice();
//                Double totalAmount = buyingPrice * collections.getQuantity();
//                collections.setAmount(totalAmount);
//                collections.setCurrentPrice(buyingPrice);
                Optional<FloatManager> manager = floatManagerRepo.findByCollectorId(collections.getCollectorId());
                if (manager.isPresent()) {
                    log.info("Collector allocation found ..");

//                    Double famount = manager.get().getFloatAmount();
//                    Double balance = famount - totalAmount;
//                    Double spent = famount - balance;
//                    manager.get().setFloatSpent(spent);
//                    manager.get().setBalance(balance);

//                    floatManagerRepo.save(manager.get());
                    MilkCollections c = milkCollectionRepo.save(collections);

                    response.setStatusCode(HttpStatus.CREATED.value());
                    response.setEntity(c);
                    response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                } else {
                    log.info("Collector allocation Not Found!! ..");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setMessage("Collector allocation Not Found!! ..");
                    return response;

                }

            } else if (event.equalsIgnoreCase("Collection")) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = formatter.format(collections.getCollectionDate());
                Integer checkDuplicate = milkCollectionRepo.checkDuplicateEntry(collections.getFarmerNo(),
                        collections.getSession(), formattedDate);
                log.info("Checking duplicate record...");

                if (checkDuplicate > 0) {
                    log.info("..Duplicate entry detected ... ");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setMessage("Duplicate entry detected");
                    return response;
                }
                Optional<FarmerInfo> check = farmerRepo.findByFarmerNo(collections.getFarmerNo());
                log.info("Checking if farmer exist ...");
                String username = "";
                if (check.isPresent()) {
                    log.info("Farmer exist ...");
                    username = check.get().getName();
                    Optional<ProductConfig> productConfig = productConfigRepo.findByRouteFk(collections.getRouteFk());
                    if (productConfig.isPresent()) {
                        Optional<Can> cancheck = canRepo.findByCanNo(collections.getCanNo());
                        if (!cancheck.isPresent()) {

                            log.info("----Collection event----");
//                            Can can = cancheck.get();
//                            Double lessWeight = Double.valueOf(can.getDeductionWeight());
                            Double lessWeight = 0.0;
                            Double actual_quantity = collections.getQuantity() - lessWeight;
                            collections.setQuantity(actual_quantity);
                            collections.setDeductedWeight(lessWeight);
                            Double buyingPrice = productConfig.get().getBuyingPrice();
                            log.info("buying price ", buyingPrice);
                            Double totalAmount = buyingPrice * collections.getQuantity();
                            collections.setOriginalQuantity(actual_quantity);
                            log.info("total amount " + totalAmount);
                            collections.setAmount(totalAmount);
                            collections.setCurrentPrice(buyingPrice);
                            //selling cost calculation
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setMessage(HttpStatus.OK.getReasonPhrase());
                        } else {
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                            response.setMessage("Can Not Found");
                            return response;
                        }
                    } else {
                        Optional<Route> r = routeRepo.findById(collections.getRouteFk());

                        log.info("Price Configuration for " + r.get().getRoute() + " Not Found");
                        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        response.setMessage("Price Configuration for " + r.get().getRoute() + " Not Found");
                        return response;
                    }
                } else {
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("Farmer Not Found");
                    return response;
                }

                MilkCollections c = milkCollectionRepo.save(collections);

                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(c);
                response.setMessage(HttpStatus.CREATED.getReasonPhrase());

                //send sms
//                if (sms) {
                Double monthTotal = milkCollectionRepo.getMonthyAccumulation(collections.getFarmerNo());
                String session = Objects.equals(collections.getSession(), "Session 1") ? "Morning" : (Objects.equals(collections.getSession(), "Session 2") ? "Afternoon" : "Evening");
                if (check.get().getMobile_no() != null) {
                    log.info("Sending sms ...");
                    String message = "Dear " + username + ", Farmer No. " + check.get().getFarmer_no() + " we have received " + collections.getQuantity() + " Kgs of milk" +
                             session + " ,Session on " + formatDate(collections.getCollectionDate()) + ". Month Total: " + monthTotal + " Kgs. Helpline: 0726777884";
                    String phoneno = check.get().getMobile_no().trim();
                    if (phoneno.startsWith("0")) {
                        log.info("Starting with 0");
                        phoneno = phoneno.replaceFirst("0", "254");
                    } else if (phoneno.startsWith("+")) {
                        log.info("Starting with +");
                        phoneno = phoneno.substring(1);
                    } else if (phoneno.startsWith("7") || phoneno.startsWith("1")) {
                        phoneno = "254" + phoneno;
                    }
                    smsServiceV2.SMSNotification(message, phoneno);
                }
            }


        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;


    }

    public EntityResponse getCollection() {

        EntityResponse response = new EntityResponse();
        try {

            List<MilkCollections> cdata = milkCollectionRepo.findAll();
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(cdata);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse updateCollections(UpdateMilkCollectiorequest col) {
        log.info("Updating milk collection ...");

        EntityResponse response = new EntityResponse();
        try {
            Optional<MilkCollections> collectionCheck = milkCollectionRepo.findByCollectionNumber(col.getCollectionNumber());
            if (collectionCheck.isPresent()) {
                MilkCollections collections= collectionCheck.get();
                Optional<ProductConfig> productConfig = productConfigRepo.findByRouteFk(collections.getRouteFk());
                if (productConfig.isPresent()) {
                    Optional<FarmerInfo> farmerInfo = farmerRepo.findByFarmerNo(collections.getFarmerNo());
                    Optional<Can> cancheck = canRepo.findByCanNo(col.getCanNo());

                    if (farmerInfo.isEmpty()) {
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                      response.setMessage("Farmer with member number "+collections.getFarmerNo()+" not found");
                      return response;
                    }
                    if (!cancheck.isPresent()) {

                        log.info("----Collection event----");
//                        Can can = cancheck.get();
//                        Double lessWeight = Double.valueOf(can.getDeductionWeight());
                        double lessWeight = 0.0;
                        Double actual_quantity = col.getOriginalQuantity() - lessWeight;
                        collections.setQuantity(actual_quantity);
                        collections.setDeductedWeight(lessWeight);
                        Double buyingPrice = productConfig.get().getBuyingPrice();
                        log.info("buying price ", +buyingPrice);
                        Double totalAmount = buyingPrice * collections.getQuantity();
                        log.info("total amount " + totalAmount);
                        collections.setSession(col.getSession());
                        collections.setCanNo(col.getCanNo());
                        collections.setAmount(totalAmount);
                        collections.setCurrentPrice(buyingPrice);
                        collections.setUpdatedStatus(CONSTANTS.YES);
                        collections.setUpdatedDate(new Date());
                        collections.setOriginalQuantity(col.getOriginalQuantity());
                        MilkCollections cdata = milkCollectionRepo.save(collections);
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(cdata);
                        response.setMessage(HttpStatus.OK.getReasonPhrase());


                        log.info("Collection for " + collections.getCollectionDate() + " was updated at: " + collections.getUpdatedDate());
                        Double monthTotal = milkCollectionRepo.getMonthyAccumulation(collections.getFarmerNo());

                        if (farmerInfo.get().getMobile_no() != null){
                            String message = "Dear "+farmerInfo.get().getName()+", M.No. "+farmerInfo.get().getFarmer_no()+"."+
                                    "\nDelivery for "+formatDate(collections.getCollectionDate())+" has been updated from "+collections.getOriginalQuantity()+" kgs to "+
                                    collections.getQuantity()+" kgs on "+formatDate(new Date())+". Monthly Total: "+monthTotal;
                            smsServiceV2.SMSNotification(message,formatPhone(farmerInfo.get().getMobile_no().trim()));
                            response.setMessage("Collection updated and sent notification to farmer.");
                            log.info("Collection updated and sent notification to farmer.");
                        }else {
                            log.info("Collection updated but failed to send notification to farmer due to unavailable phone number.");
                            response.setMessage("Collection updated but failed to send notification to farmer due to unavailable phone number.");
                        }
                    } else {
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setMessage("Can Not Found");
                    }
                } else {
                    Optional<Route> r = routeRepo.findById(collections.getRouteFk());

                    log.info("Price Configuration for " + r.get().getRoute() + " Not Found");
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    response.setMessage("Price Configuration for " + r.get().getRoute() + " Not Found");
                }
            } else {
                log.info("Milk collection Record Not Found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("Milk collection Record Not Found");
            }




        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse deleteCollections(Long id) {

        EntityResponse response = new EntityResponse();
        try {

            milkCollectionRepo.deleteById(id);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionsByMember(Integer farmerNo) {

        EntityResponse response = new EntityResponse();
        try {

            List<MilkCollections> farmerrecord = milkCollectionRepo.findByFarmerNo(farmerNo);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmerrecord);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionRecordsByPrice(Character paymentflag, Integer farmerNo) {

        EntityResponse response = new EntityResponse();
        try {

            List<AnalyticsData> farmerrecord = milkCollectionRepo.getCollectionsRecordsPrice(paymentflag, farmerNo);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmerrecord);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getAmountPerPaymentStatus(Character paymentflag, Integer farmerNo) {

        EntityResponse response = new EntityResponse();
        try {

            BigDecimal paymentAmount = milkCollectionRepo.getPaymentAmount(paymentflag, farmerNo);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(paymentAmount);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionsById(Long id) {

        EntityResponse response = new EntityResponse();
        try {

            Optional<MilkCollections> farmerrecord = milkCollectionRepo.findById(id);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmerrecord);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionsByDate(Long collectorId, String date) {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> farmerrecord = milkCollectionRepo.fetchByCollectorandDate(collectorId, date);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmerrecord);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    
    public EntityResponse filterTodaysCollections(Long collectorId, String date, String farmerNo, String session) {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> farmerrecord = milkCollectionRepo.filterTodaysCollections(collectorId, date, farmerNo, session);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmerrecord);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }


    public EntityResponse filterTodaysCollectionsBySession(Long collectorId, String date, String session) {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> farmerrecord = milkCollectionRepo.filterTodaysCollectionsBySession(collectorId, date, session);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmerrecord);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectorsPurchasesByDateRange(Long collectorId, String from, String to) {

        EntityResponse response = new EntityResponse();
        try {

            List<PurchaseData> farmerrecord = milkCollectionRepo.getCollectorsPurchasesByDateRange(collectorId, from, to);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmerrecord);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectorsPurchasesByDate(Long collectorId, String date) {

        EntityResponse response = new EntityResponse();
        try {

            List<PurchaseData> farmerrecord = milkCollectionRepo.getCollectorsPurchasesByDate(collectorId, date);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmerrecord);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionsByCollectorAndDate(Long collector, String from, String to) {

        EntityResponse response = new EntityResponse();
        try {
            List<CollectionsData> farmerrecord = milkCollectionRepo.getCollectionsByDate(collector, from, to);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmerrecord);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }


    public EntityResponse getFilteredCollections(Long collector, String farmerNo, String session, String from, String to) {

        EntityResponse response = new EntityResponse();
        try {
            List<CollectionsData> farmerrecord = milkCollectionRepo.getFilteredCollections(collector, farmerNo, session, from, to);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmerrecord);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public List<CollectionsData> fetchCollectorsCollectionsHistoryByDateRangeAndPaymentStatus(Long
                                                                                                      collectorId, String from, String to, Character paymentStatus) {
        try {

            return milkCollectionRepo.fetchCollectorsCollectionsHistoryByDateRangeAndPaymentStatus(collectorId, from, to, paymentStatus);

        } catch (Exception exception) {
            log.info("Fetching Collectors Collection Response " + exception.getLocalizedMessage());
            return null;
        }
    }

    public EntityResponse getCollectionsByColelctor(Long collectorId) {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> farmerrecord = milkCollectionRepo.findByCollectorId(collectorId);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmerrecord);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse collectionsTracker() {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionTracker> colelctionRecords = milkCollectionRepo.getCollectionTracker();
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(colelctionRecords);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse collectionsDailyRecordsPerColelctor() {

        EntityResponse response = new EntityResponse();
        try {

            List<DailyRecords> todaysCollections = milkCollectionRepo.getTodaysCollectionsPerCollector();
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(todaysCollections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse collectionsTodayRecords() {

        EntityResponse response = new EntityResponse();
        try {

            List<DailyRecords> todaysCollections = milkCollectionRepo.getTodaysCollections();
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(todaysCollections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getDayRecords(String date) {

        EntityResponse response = new EntityResponse();
        try {

            List<DailyRecords> todaysCollections = milkCollectionRepo.getSpecificDateRecord(date);
            if (todaysCollections.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }


        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getDateRangeRecords(String from,String to) {

        EntityResponse response = new EntityResponse();
        try {

            List<DailyRecords> todaysCollections = milkCollectionRepo.getDateRangeRecord(from,to);
            if (todaysCollections.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }


        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    public EntityResponse getPickUpLocationRecords(Long pickUpLocation) {

        EntityResponse response = new EntityResponse();
        try {

            List<DailyRecords> todaysCollections = milkCollectionRepo.getPickUpLocationRecord(pickUpLocation);
            if (todaysCollections.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }


        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    public EntityResponse getRouteRecords(Long routeId) {

        EntityResponse response = new EntityResponse();
        try {

            List<DailyRecords> todaysCollections = milkCollectionRepo.getRouteRecord(routeId);
            if (todaysCollections.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }


        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    public EntityResponse getRouteSummary() {

        EntityResponse response = new EntityResponse();
        try {

            List<DailyRecords> todaysCollections = milkCollectionRepo.getRouteSummary();
            if (todaysCollections.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }


        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    public EntityResponse getAllCollectionsRecords() {

        EntityResponse response = new EntityResponse();
        try {

            List<DailyRecords> todaysCollections = milkCollectionRepo.getAllColectionsRecord();
            if (todaysCollections.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }


        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    public EntityResponse getCollectionByFarmer(Long farmerId) {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> todaysCollections = milkCollectionRepo.getCollectionsbyFarmer(farmerId);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(todaysCollections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionByPickUpCollationsAndDate(Long pickuplocation, String date) {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> collections = milkCollectionRepo.getCollectionsbyPickUpLocationAndDate(pickuplocation, date);
            if (collections.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(collections);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(collections);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }


        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionByPickUpLocation(Long pickuplocation) {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> collections = milkCollectionRepo.getCollectionsbyPickUpLocation(pickuplocation);
            if (collections.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(collections);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(collections);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionByRoute(Long routeId) {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> collections = milkCollectionRepo.getCollectionsbyRoute(routeId);
            if (collections.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(collections);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(collections);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionByColector(Long collectorId) {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> todaysCollections = milkCollectionRepo.getCollectionsbyCollector(collectorId);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(todaysCollections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getBuyingCollectionByColector(Long collectorId) {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> todaysCollections = milkCollectionRepo.getBuyingCollectionsbyCollector(collectorId);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(todaysCollections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getRoleusers(Long roleId) {

        EntityResponse response = new EntityResponse();
        try {

            List<MilkCollectionRepo.Roleusers> users = milkCollectionRepo.getRoleUsers(roleId);
            if (users.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(users);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(users);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            }


        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionsBySpecificDate(String date) {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> todaysCollections = milkCollectionRepo.getCollectionsbyDate(date);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(todaysCollections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getAllCollections() {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> todaysCollections = milkCollectionRepo.getAllCollections();
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(todaysCollections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionDetailsByCollectionId(Long id) {

        EntityResponse response = new EntityResponse();
        try {

            CollectionItemData todaysCollections = milkCollectionRepo.getCollectionDetailsByCollectionId(id);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(todaysCollections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getDayCollectionsPerColector(String date) {

        EntityResponse response = new EntityResponse();
        try {

            List<AnalyticsData> todaysCollections = milkCollectionRepo.getCOllectionsPerCollectors(date);
            if (todaysCollections.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(todaysCollections);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());

            }


        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionsByDateRange(String fromDate, String toDate) {

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> todaysCollections = milkCollectionRepo.getCollectionByDateRange(fromDate, toDate);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(todaysCollections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionsRoutes(Long collectorId, String date) {

        EntityResponse response = new EntityResponse();
        try {

            List<RouteData> routes = milkCollectionRepo.getCollectorRoutes(collectorId, date);
            if (routes.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(routes);
                response.setMessage(HttpStatus.OK.getReasonPhrase());

            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(routes);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());

            }

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse fetchCurrentAndPreviousCollectionsAndFarmersCount(Integer collectorId, String currentDate, String previousDayDate) {
        EntityResponse response = new EntityResponse();
        try {

            CurrentTotalCollections currentTotalCollections = milkCollectionRepo.findCurrentTotalCollections(collectorId, currentDate);
            CurrentTotalCollections previousTotalCollections = milkCollectionRepo.findCurrentTotalCollections(collectorId, previousDayDate);

            List<CurrentTotalCollections> totalCollectionsList = new ArrayList<>();
            totalCollectionsList.add(currentTotalCollections);
            totalCollectionsList.add(previousTotalCollections);


            LocalDate date = LocalDate.parse(currentDate);
            int month = date.getMonthValue();

            LocalDate currDate = LocalDate.parse(currentDate);
            LocalDate prevDate = currDate.minusMonths(1);
            int prevMonth = prevDate.getMonthValue();

            CurrentTotalFarmers currentTotalFarmers = farmerRepo.fetchCurrentAndPreviousCollectionsAndFarmersCount(collectorId, month);
            CurrentTotalFarmers previousTotalFarmers = farmerRepo.fetchCurrentAndPreviousCollectionsAndFarmersCount(collectorId, prevMonth);

            List<CurrentTotalFarmers> totalFarmersList = new ArrayList<>();
            totalFarmersList.add(currentTotalFarmers);
            totalFarmersList.add(previousTotalFarmers);

            TotalCollectionsFarmers totalCollectionsFarmers = new TotalCollectionsFarmers();
            totalCollectionsFarmers.setTotalCollections(totalCollectionsList);
            totalCollectionsFarmers.setTotalFarmers(totalFarmersList);

            response.setEntity(totalCollectionsFarmers);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Records Found");

            return response;

        }catch (Exception exc){
            log.error(exc.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

//    data for route per center broken into sessions
    public List<AnalyticsData> getRouteSummaryForCenter(String date, Long centerId) {
        try {
            return milkCollectionRepo.getRouteSummaryForCenter(date, centerId);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
}



