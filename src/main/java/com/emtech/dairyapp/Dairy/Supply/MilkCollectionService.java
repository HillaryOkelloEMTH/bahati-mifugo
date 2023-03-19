package com.emtech.dairyapp.Dairy.Supply;

import com.emtech.dairyapp.Configurations.ProductConfig.ProductConfig;
import com.emtech.dairyapp.Configurations.ProductConfig.ProductConfigRepo;
import com.emtech.dairyapp.Dairy.FloatTracking.FloatManager;
import com.emtech.dairyapp.Dairy.FloatTracking.FloatManagerRepo;
import com.emtech.dairyapp.Dairy.Interface.CollectionTracker;
import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.Interface.DailyRecords;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MilkCollectionService {


    private final MilkCollectionRepo milkCollectionRepo;
    private final ProductConfigRepo productConfigRepo;
    private final FloatManagerRepo floatManagerRepo;


    public MilkCollectionService(MilkCollectionRepo milkCollectionRepo, ProductConfigRepo productConfigRepo, FloatManagerRepo floatManagerRepo) {
        this.milkCollectionRepo = milkCollectionRepo;
        this.productConfigRepo = productConfigRepo;
        this.floatManagerRepo = floatManagerRepo;
    }



    public EntityResponse newcollection(MilkCollections collections){

        EntityResponse response = new EntityResponse();
        try{

            collections.setProductType("Milk");
            collections.setEvent("Buying");
            String event= collections.getEvent();

           Optional<ProductConfig> productConfig =productConfigRepo.findByProductName(collections.getProductType().trim());
           if(productConfig.isPresent()) {
               if (event.equalsIgnoreCase("Buying")) {
                   log.info("buying event");
                   Double buyingPrice = productConfig.get().getBuyingPrice();
                   log.info("buying price ", +buyingPrice);
                   Double totalAmount = buyingPrice * collections.getQuantity();
                   log.info("total amount " + totalAmount);
                   collections.setAmount(totalAmount);
                   collections.setCurrentPrice(buyingPrice);
                   log.info("Getting float management configurations....");
                   Optional<FloatManager> manager = floatManagerRepo.findByCollectorId(collections.getCollectorId());
                   if (manager.isPresent()) {
                       log.info("Collector allocation found ..");
                       Double famount = manager.get().getFloatAmount();
                       Double balance = famount - totalAmount;
                       manager.get().setBalance(balance);
                       floatManagerRepo.save(manager.get());
                   } else {
                       log.info("Collector allocation Not Found!! ..");
                       response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                       response.setMessage(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase());

                   }

               } else {
                   log.info("selling event");

                   //selling cost calculation


                   response.setStatusCode(HttpStatus.OK.value());
                   response.setMessage(HttpStatus.OK.getReasonPhrase());

               }

               MilkCollections c = milkCollectionRepo.save(collections);
               response.setStatusCode(HttpStatus.CREATED.value());
               response.setEntity(c);
               response.setMessage(HttpStatus.CREATED.getReasonPhrase());
           }else {
               log.info("Product Configuration Not Found!");
               response.setStatusCode(HttpStatus.BAD_REQUEST.value());
               response.setMessage("Product Configuration Not Found!");
           }
        }catch (Exception e){
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
    public EntityResponse getCollectionsByMember(Long memberid) {

        EntityResponse response = new EntityResponse();
        try {

           List<MilkCollections> farmerrecord= milkCollectionRepo.findByMember(memberid);
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
    public EntityResponse getCollectionsById(Long id) {

        EntityResponse response = new EntityResponse();
        try {

            Optional<MilkCollections> farmerrecord= milkCollectionRepo.findById(id);
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
    public EntityResponse getCollectionsByDate(Long collectorId,String date ){

        EntityResponse response = new EntityResponse();
        try {

            List<MilkCollections> farmerrecord= milkCollectionRepo.fetchByCollectorandDate(collectorId,date);
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
    public EntityResponse getCollectionsByCollectorAndDate(Long collector,String from ,String to ){

        EntityResponse response = new EntityResponse();
        try {
            List<MilkCollections> farmerrecord= milkCollectionRepo.getCollectionsByDate(collector,from,to);
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

    public EntityResponse getCollectionsByColelctor(Long collectorId){

        EntityResponse response = new EntityResponse();
        try {

            List<MilkCollections> farmerrecord= milkCollectionRepo.findByCollectorId(collectorId);
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
    public EntityResponse collectionsTracker(){

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionTracker> colelctionRecords= milkCollectionRepo.getCollectionTracker();
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
    public EntityResponse collectionsDailyRecordsPerColelctor(){

        EntityResponse response = new EntityResponse();
        try {

            List<DailyRecords> todaysCollections= milkCollectionRepo.getTodaysCollectionsPerCollector();
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
    public EntityResponse collectionsTodayRecords(){

        EntityResponse response = new EntityResponse();
        try {

            List<DailyRecords> todaysCollections= milkCollectionRepo.getTodaysCollections();
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
    public EntityResponse getCollectionByFarmer(Long farmerId){

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> todaysCollections= milkCollectionRepo.getCollectionsbyFarmer(farmerId);
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
    public EntityResponse getCollectionByColector(Long collectorId){

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> todaysCollections= milkCollectionRepo.getCollectionsbyCollector(collectorId);
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
    public EntityResponse getCollectionsBySpecificDate(String date){

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> todaysCollections= milkCollectionRepo.getCollectionsbyDate(date);
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
    public EntityResponse getCollectionsByDateRange(String fromDate,String toDate){

        EntityResponse response = new EntityResponse();
        try {

            List<CollectionsData> todaysCollections= milkCollectionRepo.getCollectionByDateRange(fromDate, toDate);
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



}



