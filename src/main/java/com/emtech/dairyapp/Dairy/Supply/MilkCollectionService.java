package com.emtech.dairyapp.Dairy.Supply;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

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

    }
