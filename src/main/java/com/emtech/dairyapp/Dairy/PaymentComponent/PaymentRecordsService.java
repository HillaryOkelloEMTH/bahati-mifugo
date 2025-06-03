package com.emtech.dairyapp.Dairy.PaymentComponent;

import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PaymentRecordsService {

    @Autowired
    private MilkCollectionRepo collectionRepo;

    public EntityResponse getFarmerPaymentData() {

        EntityResponse response = new EntityResponse();
        try {

            List<PaymentFileData> cdata = collectionRepo.getFarmersPaymentRecords();
            if(cdata.size()>0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(cdata);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(cdata);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    public EntityResponse getFilteredFarmerPaymentData(String month,String mode,Character paymentStatus) {

        EntityResponse response = new EntityResponse();
        try {

            List<PaymentFileData> cdata = collectionRepo.getFilteredFarmersPaymentRecords(month,mode,paymentStatus);
            if(cdata.size()>0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(cdata);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(cdata);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

//    Fetch Farmer Payment Data By Collection Center.
    public EntityResponse getFilterFarmerPaymentDataByLocation(Long locationId){

        EntityResponse response = new EntityResponse();

        try{
            List<PaymentFileData> paymentData = collectionRepo.getFilteredPaymentRecordsByLocation(locationId);

            if(paymentData.size()>0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(paymentData);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(paymentData);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;


    }

    public EntityResponse getFilterFarmerPaymentDataByFarmerNo(String farmerNo){

        EntityResponse response = new EntityResponse();

        try{
            List<PaymentFileData> paymentData = collectionRepo.getFilteredPaymentRecordsByFarmer(farmerNo);

            if(paymentData.size() > 0){
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(paymentData);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            }else{
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(paymentData);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }
        } catch (Exception e){
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }


}
