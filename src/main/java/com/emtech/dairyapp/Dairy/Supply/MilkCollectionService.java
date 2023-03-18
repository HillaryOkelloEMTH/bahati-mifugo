package com.emtech.dairyapp.Dairy.Supply;

import com.emtech.dairyapp.Configurations.ProductConfig.ProductConfig;
import com.emtech.dairyapp.Configurations.ProductConfig.ProductConfigRepo;
import com.emtech.dairyapp.Dairy.FloatTracking.FloatManager;
import com.emtech.dairyapp.Dairy.FloatTracking.FloatManagerRepo;
import com.emtech.dairyapp.Dairy.Interface.CollectionTracker;
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
            ProductConfig productConfig =productConfigRepo.findByProductName(collections.getProductType());
            String event= collections.getEvent();
            if(event.equalsIgnoreCase("Buying")){
                log.info("buying event");
                Double buyingPrice= productConfig.getBuyingPrice();
                Double totalAmount= buyingPrice*collections.getQuantity();
                collections.setAmount(totalAmount);
                collections.setCurrentPrice(buyingPrice);

                FloatManager manager = floatManagerRepo.findByCollectorId(collections.getCollectorId());
                Double famount= manager.getFloatAmount();
                Double balance = famount-totalAmount;
                manager.setBalance(balance);

                floatManagerRepo.save(manager);

            }else {
                log.info("selling event");

                //selling cost calculation


                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.OK.getReasonPhrase());

            }

            MilkCollections c= milkCollectionRepo.save(collections);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(c);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        }catch (Exception e){
            log.error(e.getMessage());
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
    public EntityResponse collectionsDailyRecords(){

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



}



