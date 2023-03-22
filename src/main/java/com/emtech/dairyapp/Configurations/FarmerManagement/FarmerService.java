package com.emtech.dairyapp.Configurations.FarmerManagement;

import com.emtech.dairyapp.Analytics.LinkedStringInteger;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmersPerWard;
import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FarmerService {




    private final FarmerRepo farmerRepo;


    public static String generatecSystemCode(int len) {
        String chars = "01234567890";
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
            String username = farmer.getFirstName()+ " " +farmer.getLastName();
            farmer.setUsername(username);
            StringBuilder sb=new StringBuilder();
            LocalDate date = LocalDate.now();
            String year = String.valueOf(date.getYear()).substring(2,4);
            Random random = new Random();
           Integer val= random.nextInt(100);
           log.info(val.toString());
            Long maxValue = farmerRepo.getMaxVaue()+1;
            String code=  sb.append(year).append(val).append(maxValue).toString();
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
    public EntityResponse fetchFarmerByMemberNO(String memberNO) {
        log.info("Fetching Farmers ...");
        EntityResponse response = new EntityResponse();
        try {
            Optional<FarmerInfo> farmer = farmerRepo.findByMemberCode(memberNO);
            if(farmer.isPresent()) {
                log.info("Farmers Found "+ "("+farmer.get().getUsername()+")");
                response.setEntity(farmer.get());
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                log.info("Farmers Not Found "+ "("+farmer.get().getUsername()+")");
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


    public LinkedStringInteger farmersPerLocation(){

        LinkedStringInteger data = new LinkedStringInteger();
        try{

            List<FarmersPerWard> farmersPerWards = farmerRepo.getFarmersPerWard();





            LinkedList<String> names= new LinkedList<>();
            LinkedList<Integer> counts= new LinkedList<>();

            for (FarmersPerWard f:farmersPerWards ) {
//                    map.put(a.getRole(),a.getUsers());

                names.add(f.getWard());
                counts.add(f.getFarmers());

            }
            data.setCount(counts);
            data.setNames(names);
            return  data;

        }catch (Exception e){
            log.error(e.getMessage());
            return data;
        }
    }


}
