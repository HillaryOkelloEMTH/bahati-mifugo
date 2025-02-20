package com.emtech.dairyapp.Configurations.FarmerManagement;

import com.emtech.dairyapp.Analytics.LinkedStringInteger;
import com.emtech.dairyapp.Configurations.Interfaces.*;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocationsRepo;
import com.emtech.dairyapp.Configurations.Routes.Route;
import com.emtech.dairyapp.Configurations.Routes.RouteRepo;
import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import com.emtech.dairyapp.Configurations.Utils.Formatter;
import com.emtech.dairyapp.Notifications.SMS.smsv2.SmsServiceV2;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

//import jakarta.swing.text.html.parser.Entity;
import java.util.*;

@Service
@Slf4j
public class FarmerService {
    @Value("${sms.enable}")
    private boolean sms;



    private final FarmerRepo farmerRepo;

    private final RouteRepo routeRepo;

    private final SmsServiceV2 smsServiceV2;

    private final PickUpLocationsRepo pickUpLocationsRepo;


//    public static String generatecSystemCode(int len) {
//        String chars = "01234567890";
//        Random rnd = new Random();
//        String S = "S";
//        StringBuilder sb = new StringBuilder(len);
//        for (int i = 0; i < 10; i++)
//            sb.append(chars.charAt(rnd.nextInt(chars.length()))).toString();
//        return S + sb;
//    }

    public FarmerService(FarmerRepo farmerRepo, SmsServiceV2 smsServiceV2, RouteRepo routeRepo, PickUpLocationsRepo pickUpLocationsRepo) {
        this.farmerRepo = farmerRepo;
        this.smsServiceV2 = smsServiceV2;
        this.routeRepo = routeRepo;
        this.pickUpLocationsRepo = pickUpLocationsRepo;
    }

    public EntityResponse<?> addFarmer(Farmer farmer){
        log.info("Adding new Farmer ...");
        EntityResponse<Farmer> response = new EntityResponse<>();
        try{
            String username = farmer.getFirstName()+ " " +farmer.getLastName();
            farmer.setUsername(username);
            Integer count = farmerRepo.getCount();

            if (Objects.nonNull(farmer.getIdNumber()) && !"".equalsIgnoreCase(farmer.getIdNumber())){
                log.info("checking if farmer with farmer with id no "+farmer.getIdNumber()+" exists");
                Integer existsById = farmerRepo.farmerExistsById(farmer.getIdNumber());

                if (existsById > 0){
                    response.setMessage("The provided ID number is registered with a different farmer");
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    return response;
                }
            }

            if (Objects.nonNull(farmer.getMobileNo()) && !"".equalsIgnoreCase(farmer.getMobileNo())){
                log.info("checking if farmer with farmer with mobile "+farmer.getMobileNo()+" exists");
                Integer existsByMobile = farmerRepo.farmerExistsByMobile(farmer.getMobileNo());

                if (existsByMobile > 0){
                    response.setMessage("The provided Mobile Number is registered with a different farmer");
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    return response;
                }
            }

            Integer memberNO=null;
            if (count > 0) {
                Integer max = farmerRepo.getMaxVaue()+1;
                log.info("Max id "+ max);
                memberNO= max;
                for (int i = 0; i < 5; i++) { // loop 10 times
                    log.info("Initial member No "+ memberNO);
                    if (farmerRepo.existsByFarmerNo(memberNO)) {
                        log.info("Member No "+ memberNO+ " already exist");
                        memberNO = memberNO + 1;
                        log.info("New member No "+ memberNO);

                    }else {
                        log.info("Member No "+ memberNO+ " does not exist");
                        log.info("Setting Member No "+ memberNO+ " ...");
                        farmer.setFarmerNo(memberNO);
                        break;
                    }


                }

            } else {
                memberNO=1;
                farmer.setFarmerNo(memberNO);

            }

            farmer.setCreatedAt(new Date());
            farmer.setDeletedFlag(CONSTANTS.NO);
            farmerRepo.save(farmer);
            log.info("Saving Farmer ...");
            response.setEntity(farmer);
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
//            if (sms) {
                log.info("Sending sms ...");

                String message = "Dear " + username + ", your registration was successful. Your member number is " + farmer.getFarmerNo() + "." +
                        " Bank: "+farmer.getBankDetails().getBankName()+" Acc no: "+farmer.getBankDetails().getAccountNumber() +
                        " Welcome to Bahati Dairies";
                String phoneno = farmer.getMobileNo().trim();
                if (phoneno.startsWith("0")) {
                    log.info("Starting with 0");
                    phoneno = phoneno.replaceFirst("0", "254");
                } else if (phoneno.startsWith("+")) {
                    log.info("Starting with +");
                    phoneno = phoneno.substring(1, phoneno.length());
                } else if (phoneno.startsWith("7") || phoneno.startsWith("1")) {
                    phoneno = "254" + phoneno;
                }
//                smsServiceV2.SMSNotification(message, phoneno);
//            }
            log.info("Farmer Added");
            return response;


        }catch (Exception e){
            e.printStackTrace();
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse<?> getFarmerData(Integer farmerNo) {
        EntityResponse<FarmerData> response = new EntityResponse<>();

        try {
            Optional<FarmerData> optional = farmerRepo.getFarmerData(farmerNo);

            if (optional.isEmpty()) {
                response.setMessage("Farmer with farmer no "+farmerNo+" not found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return response;
            }

            response.setMessage("Retrieved farmer");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(optional.get());
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("An error occurred");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    public EntityResponse<?> getByFarmerNo(Integer farmerNo) {
        EntityResponse<Farmer> response = new EntityResponse<>();

        try {
            Optional<Farmer> optionalFarmer = farmerRepo.getByFarmerNo(farmerNo);

            if (optionalFarmer.isEmpty()) {
                response.setMessage("Farmer with member no "+farmerNo+ "not found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return response;
            }

            response.setMessage("Farmer found");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(optionalFarmer.get());
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("An error occurred");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }


    public EntityResponse fetchFarmer() {
        log.info("Fetching Farmers ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Farmer> Farmers = farmerRepo.findByDeletedFlag(CONSTANTS.NO);
//            List<Farmer> Farmers = farmerRepo.findAll();
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
    public EntityResponse findById(Long farmerId) {
        log.info("Fetching Farmers ...");
        EntityResponse response = new EntityResponse();
        try {
            Optional<Farmer> farmer = farmerRepo.findById(farmerId);
            if(farmer.isPresent()) {
//                log.info("Farmers Found "+ "("+farmer.get().getUsername()+")");
                response.setEntity(farmer.get());
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                log.info("Farmers Not Found ");
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

    public EntityResponse<?> getMccFarmers(Long locationId) {
        EntityResponse<List<FarmerInterface>> response = new EntityResponse<>();

        try {
            boolean exists = pickUpLocationsRepo.existsById(locationId);

            if (!exists) {
                response.setMessage("pick up location with id "+locationId+" not found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return  response;
            }

            List<FarmerInterface> farmers = farmerRepo.getMccfarmers(locationId);

            response.setMessage("Found "+farmers.size()+" farmers");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmers);
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("failed to get farmers");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    public EntityResponse<?> getRouteFarmers(Long routeId) {
        EntityResponse<List<Farmer>> response = new EntityResponse<>();

        try {
            List<Farmer> farmers = farmerRepo.getFarmersPerRoute(routeId);

            response.setMessage("Found "+farmers.size()+" farmers");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(farmers);
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("An error occurred");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }
    public EntityResponse fetchFarmers() {
        log.info("Fetching Farmers ...");
        EntityResponse response = new EntityResponse();
        try {
            List<FarmerInfo> farmers = farmerRepo.getAllfarmers();
            if(farmers.size()>0) {
//                log.info("Farmers Found "+ "("+farmer.get().getUsername()+")");
                response.setEntity(farmers);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                log.info("Farmers Not Found ");
                response.setEntity(farmers);
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
    public EntityResponse fetchFarmerByMemberNO(Integer memberNO) {
        log.info("Fetching Farmer with Farmer No. "+ memberNO );
        EntityResponse response = new EntityResponse();
        try {
            Optional<FarmerInfo> farmer = farmerRepo.findByFarmerNo(memberNO);
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
    public EntityResponse fetchFarmerByfarmerNo(Integer farmerNo,Long collectorId) {
        log.info("Fetching Farmer with Farmer No."+ farmerNo );
        EntityResponse response = new EntityResponse();
        try {
            boolean farmerexist= farmerRepo.existsByFarmerNo(farmerNo);
            if(farmerexist) {

                List<Farmer> collectorfarmers = farmerRepo.getfarmersPerCollector(collectorId);
                log.info("Collector " + collectorId + " Farmers size " + collectorfarmers.size());
                boolean farmerCheck = collectorfarmers.stream().anyMatch(f -> f.getFarmerNo().equals(farmerNo));
                if (farmerCheck) {

                    Optional<FarmerInfo> farmer = farmerRepo.findByFarmerNo(farmerNo);
                    if (farmer.isPresent()) {
                        log.info("Farmers Found " + "(" + farmer.get().getUsername() + ")");
                        response.setEntity(farmer.get());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    } else {
                        log.info("Farmers Not Found " + "(" + farmer.get().getUsername() + ")");
                        response.setEntity(farmer);
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
                    }
                } else {
                    log.info("Farmer number " + farmerNo + " belongs to another pick up location");

                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setMessage("Farmer number " + farmerNo + " belongs to another pick up location");

                }
            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("Farmer number " + farmerNo + " does not exist");
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

    public EntityResponse<?> updateFarmerRoute(Integer farmerNo, Long routeId) {
        EntityResponse<String> response = new EntityResponse<>();

        try {
            log.info("checking farmer existence .......");
            Optional<Farmer> optionalFarmer = farmerRepo.getByFarmerNo(farmerNo);

            if (optionalFarmer.isEmpty()) {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setEntity("farmer with farmer no "+farmerNo+" not found");
                return response;
            }

            Optional<Route> optionalRoute = routeRepo.findById(routeId);

            if (optionalRoute.isEmpty()) {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setEntity("Route with id "+routeId+" not found");
                return response;
            }

            Route route = optionalRoute.get();
            Farmer farmer = optionalFarmer.get();
            farmer.setRouteFk(routeId);
            farmerRepo.save(farmer);

            if (farmer.getMobileNo() != null) {
                String phoneNo = Formatter.formatPhone(farmer.getMobileNo());

                String message = "Dear "+farmer.getFirstName()+", member no "+farmer.getFarmerNo()+" your route has been updated to "+route.getRoute();

//                smsServiceV2.SMSNotification(message, phoneNo);
            }


            response.setMessage("ok");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity("Farmer route updated to "+route.getRoute());
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("Bad Request");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    public EntityResponse updateFarmer(Farmer farmer) {
        EntityResponse response = new EntityResponse();
        try {
            String username = farmer.getFirstName()+ " " +farmer.getLastName();
            farmer.setUsername(username);
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

    public EntityResponse<?> fetchFarmerByCollector(Long collectorId) {
        log.info("Fetching Farmers ...");
        EntityResponse<List<Farmer>> response = new EntityResponse<>();
        try {
            List<Farmer> farmer = farmerRepo.getfarmersPerCollector(collectorId);

            if(!farmer.isEmpty()) {
                response.setEntity(farmer);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                response.setEntity(farmer);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse<?> getFarmersPerTransporter(Long transporterId) {
        EntityResponse<List<Farmer>> response = new EntityResponse<>();

        try {
            List<Farmer> farmers = farmerRepo.getFarmersPerTransporter(transporterId);

            response.setEntity(farmers);
            response.setMessage("Found "+farmers.size()+" farmers");
            response.setStatusCode(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("An error occurred");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
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
    public EntityResponse fetchFarmerAccrualAmount(Long farmerId) {
        log.info("Fetching Accrued information ...");
        EntityResponse response = new EntityResponse();
        try {
             FarmerAccruedAmount data= farmerRepo.getFarmerAccruedAmount(farmerId,CONSTANTS.NO);
                response.setEntity(data);
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




}
