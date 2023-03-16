package com.emtech.dairyapp.Dairy.Supply;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MilkCollectionService {


    private final MilkCollectionRepo milkCollectionRepo;

    public MilkCollectionService(MilkCollectionRepo milkCollectionRepo) {
        this.milkCollectionRepo = milkCollectionRepo;
    }



    public EntityResponse newcollection(MilkCollections collections){

        EntityResponse response = new EntityResponse();
        try{

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
    public EntityResponse getCollectionsByDate(Date date,Long id ){

        EntityResponse response = new EntityResponse();
        try {

            List<MilkCollections> farmerrecord= milkCollectionRepo.findByCollectionDateAndAndCollectorId(date,id);
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
//    public EntityResponse getCollectionsByCollectorAndDate(Long collector,Date from ,Date to ){
//
//        EntityResponse response = new EntityResponse();
//        try {
//
//            List<MilkCollections> farmerrecord= milkCollectionRepo.findByCollectionDateAndAndCollectorId(collector,from,to);
//            response.setStatusCode(HttpStatus.OK.value());
//            response.setEntity(farmerrecord);
//            response.setMessage(HttpStatus.OK.getReasonPhrase());
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
//        }
//        return response;
//    }
//

}



