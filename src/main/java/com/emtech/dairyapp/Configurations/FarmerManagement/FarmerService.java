package com.emtech.dairyapp.Configurations.FarmerManagement;

import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class FarmerService {




    private final FarmerRepo farmerRepo;


    public static String generatecSystemCode(int len) {
        String chars = "01234567890GOODWAY";
        Random rnd = new Random();
        String S = "S";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length()))).toString();
        return S + sb;
    }

    public FarmerService(FarmerRepo farmerRepo) {
        this.farmerRepo = farmerRepo;
    }

    public EntityResponse addFarmer(Farmer farmer){
        log.info("Adding new Farmer ...");
        EntityResponse response = new EntityResponse();
        try{
            StringBuilder sb=new StringBuilder();
            LocalDate date = LocalDate.now();
            String year = String.valueOf(date.getYear()).substring(2,4);
            Long maxValue = farmerRepo.getMaxVaue()+1;
            String code=  sb.append(year).append(maxValue).toString();
            farmer.setMemberCode(code);
            farmer.setCreatedAt(new Date());
            farmer.setDeletedFlag(CONSTANTS.NO);
            farmerRepo.save(farmer);
            log.info("Saving Farmer ...");
            response.setEntity(farmer);
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
            return response;


        }catch (Exception e){
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse fetchFarmer() {
        log.info("Fetching Farmers ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Farmer> Farmers = farmerRepo.findByDeletedFlag(CONSTANTS.NO);
            if(Farmers.size()>0) {
                log.info("Farmers Found "+ "("+Farmers.size()+")");
                response.setEntity(Farmers);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                log.info("Farmers Not Found "+ "("+Farmers.size()+")");
                response.setEntity(Farmers);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse fetchFarmerById(Long farmerId) {
        log.info("Fetching Farmers ...");
        EntityResponse response = new EntityResponse();
        try {
            Optional<FarmerInfo> farmer = farmerRepo.getfarmerDetails(farmerId);
            if(farmer.isPresent()) {
//                log.info("Farmers Found "+ "("+farmer.get().getUsername()+")");
                response.setEntity(farmer.get());
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                log.info("Farmers Not Found "+ "("+farmer.get().getCounty()+")");
                response.setEntity(farmer);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse fetchFarmersByward(Long wardId) {
        log.info("Fetching Farmers ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Farmer> Farmers = farmerRepo.findByWardFk(wardId);
            if(Farmers.size()>0) {
                log.info("Farmers Found "+ "("+Farmers.size()+")"+ " in ward "+ wardId);
                response.setEntity(Farmers);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                log.info("Farmers Not Found "+ "("+Farmers.size()+")");
                response.setEntity(Farmers);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse updateFarmer(Farmer farmer) {
        EntityResponse response = new EntityResponse();
        try {
            farmer.setCreatedAt(new Date());
            Farmer f= farmerRepo.save(farmer);
            response.setEntity(f);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            return response;


        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse deleteFarmer(Long id) {
        EntityResponse response = new EntityResponse();
        try {
            Optional<Farmer> Farmer = farmerRepo.findById(id);
            if(Farmer.isPresent()){
                Farmer.get().setDeletedFlag(CONSTANTS.YES);
                Farmer.get().setDeletedOn(new Date());
                farmerRepo.save(Farmer.get());
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Farmer deleted Successfully");
                return response;

            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("Farmer with id "+id+"Not Found");
                return response;

            }
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse fetchFarmerByCollector(Long collectorId) {
        log.info("Fetching Farmers ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Farmer> farmer = farmerRepo.getfarmersPerCollector(collectorId);
            if(farmer.size()>0) {
                response.setEntity(farmer);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {

                response.setEntity(farmer);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }



}
