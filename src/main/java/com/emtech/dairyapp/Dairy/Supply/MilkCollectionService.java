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
import com.emtech.dairyapp.Dairy.FloatTracking.FloatManager;
import com.emtech.dairyapp.Dairy.FloatTracking.FloatManagerRepo;
import com.emtech.dairyapp.Dairy.Interface.*;
import com.emtech.dairyapp.Notifications.SMS.SMSService;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MilkCollectionService {


    private final MilkCollectionRepo milkCollectionRepo;
    private final ProductConfigRepo productConfigRepo;
    private final FloatManagerRepo floatManagerRepo;
    private final Codenerator codenerator;
    private final SMSService smsservice;
    private final FarmerRepo farmerRepo;
    private final CanRepo canRepo;
    private final RouteRepo routeRepo;

    @Value("${sms.enable}")
    private boolean sms;


    public MilkCollectionService(MilkCollectionRepo milkCollectionRepo, ProductConfigRepo productConfigRepo, FloatManagerRepo floatManagerRepo, Codenerator codenerator, SMSService smsservice, FarmerRepo farmerRepo, CanRepo canRepo, RouteRepo routeRepo) {
        this.milkCollectionRepo = milkCollectionRepo;
        this.productConfigRepo = productConfigRepo;
        this.floatManagerRepo = floatManagerRepo;
        this.codenerator = codenerator;
        this.smsservice = smsservice;
        this.farmerRepo = farmerRepo;
        this.canRepo = canRepo;
        this.routeRepo = routeRepo;
    }


    public EntityResponse newcollection(MilkCollections collections) {

        EntityResponse response = new EntityResponse();
        try {


            String collectionNumber = codenerator.codeGenerator();
            collections.setCollectionNumber(collectionNumber);


            collections.setProductType("Milk");
            String event = collections.getEvent();

            log.info("Price management fro route found...");
            if (event.equalsIgnoreCase("Buying")) {
                log.info("buying event");
                collections.setQuantity(collections.getOriginalQuantity());
                Double buyingPrice = collections.getCurrentPrice();
                Double totalAmount = buyingPrice * collections.getQuantity();
                collections.setAmount(totalAmount);
                collections.setCurrentPrice(buyingPrice);
                Optional<FloatManager> manager = floatManagerRepo.findByCollectorId(collections.getCollectorId());
                if (manager.isPresent()) {
                    log.info("Collector allocation found ..");

                    Double famount = manager.get().getFloatAmount();
                    Double balance = famount - totalAmount;
                    Double spent = famount - balance;
                    manager.get().setFloatSpent(spent);
                    manager.get().setBalance(balance);

                    floatManagerRepo.save(manager.get());
                    MilkCollections c = milkCollectionRepo.save(collections);

                    response.setStatusCode(HttpStatus.CREATED.value());
                    response.setEntity(c);
                    response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                } else {
                    log.info("Collector allocation Not Found!! ..");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setMessage(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase());

                }

            } else if (event.equalsIgnoreCase("Collection")) {
                boolean checkDuplicate = milkCollectionRepo.existsByFarmerNoAndQuantityAndSessionAndCollectorId(collections.getFarmerNo(),
                        collections.getQuantity(), collections.getSession(), collections.getCollectorId());
                log.info("Checking duplicate record...");

                if (checkDuplicate) {
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
                    username = check.get().getUsername();
                    Optional<ProductConfig> productConfig = productConfigRepo.findByRouteFk(collections.getRouteFk());
                    if (productConfig.isPresent()) {
                        Optional<Can> cancheck = canRepo.findByCanNo(collections.getCanNo());
                        if (cancheck.isPresent()) {

                            log.info("----Collection event----");
                            Can can = cancheck.get();
                            Double lessWeight = Double.valueOf(can.getDeductionWeight());
                            Double actual_quantity = collections.getOriginalQuantity() - lessWeight;
                            collections.setQuantity(actual_quantity);
                            collections.setDeductedWeight(lessWeight);
                            Double buyingPrice = productConfig.get().getBuyingPrice();
                            log.info("buying price ", +buyingPrice);
                            Double totalAmount = buyingPrice * collections.getQuantity();
                            log.info("total amount " + totalAmount);
                            collections.setAmount(totalAmount);
                            collections.setCurrentPrice(buyingPrice);
                            //selling cost calculation
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setMessage(HttpStatus.OK.getReasonPhrase());
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
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("Farmer Not Found");
                }

                MilkCollections c = milkCollectionRepo.save(collections);

                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(c);
                response.setMessage(HttpStatus.CREATED.getReasonPhrase());

                //send sms
                if (sms) {
                    log.info("Sending sms ...");
                                String message = "Dear " + username + ", we have received your " + collections.getQuantity() + " of milk" +
                                        " collections for " + collections.getSession() + " at " + collections.getCollectionDate() + ".";
                                String phoneno = check.get().getMobile_no().trim();
                                if (phoneno.startsWith("0")) {
                                    log.info("Starting with 0");
                                    phoneno = phoneno.replaceFirst("0", "254");
                                } else if (phoneno.startsWith("+")) {
                                    log.info("Starting with +");
                                    phoneno = phoneno.substring(1, phoneno.length());
                                } else if (phoneno.startsWith("7") || phoneno.startsWith("1")) {
                                    phoneno = "254" + phoneno;
                                }
                                smsservice.SMSNOtification(message, phoneno);
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

    public EntityResponse updateCollections(MilkCollections collections) {

        EntityResponse response = new EntityResponse();
        try {


            MilkCollections cdata = milkCollectionRepo.save(collections);
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
}



