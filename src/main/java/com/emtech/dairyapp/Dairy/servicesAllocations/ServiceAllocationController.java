package com.emtech.dairyapp.Dairy.servicesAllocations;


import com.emtech.dairyapp.Configurations.FarmerManagement.Farmer;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.servicesConfig.ServicesConfig;
import com.emtech.dairyapp.Configurations.servicesConfig.ServicesConfigController;
import com.emtech.dairyapp.Configurations.servicesConfig.ServicesConfigRepository;
import com.emtech.dairyapp.Dairy.Interface.Allocations;
import com.emtech.dairyapp.Dairy.Interface.Services;
import com.emtech.dairyapp.Response.EntityResponse;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/service-allocation")
@Api(tags = "Service Allocation Controller")
public class ServiceAllocationController {

    private final Logger LOG = LoggerFactory.getLogger(ServicesConfigController.class);
    private final EntityResponse response = new EntityResponse();


    @Autowired
    private ServicesConfigRepository servicesConfigRepository;

    @Autowired
    private ServicesAllocationService allocationService;


    @GetMapping("services/fetch-all")
    public ResponseEntity<?> fetchAllServices(){
        List<Services> allocationsList = allocationService.fetchAllServices();
        if (allocationsList.size() > 0){
            response.setMessage(allocationsList.size() + " Records Found ");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(allocationsList);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else {
            response.setMessage("No Records Found ");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(allocationsList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("services/fetch-all/status")
    public ResponseEntity<?> fetchAllServicesByServiceStatus(@RequestParam String status){
        List<Services> allocationsList = allocationService.fetchAllServicesByServiceStatus(status);
        if (allocationsList.size() > 0){
            response.setMessage(allocationsList.size() + " Records Found ");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(allocationsList);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else {
            response.setMessage("No Records Found ");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(allocationsList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

}
