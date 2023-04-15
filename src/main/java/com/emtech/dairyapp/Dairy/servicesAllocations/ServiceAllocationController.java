package com.emtech.dairyapp.Dairy.servicesAllocations;


import com.emtech.dairyapp.Configurations.servicesConfig.ServicesConfig;
import com.emtech.dairyapp.Configurations.servicesConfig.ServicesConfigController;
import com.emtech.dairyapp.Configurations.servicesConfig.ServicesConfigRepository;
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

@RestController
@RequestMapping("api/service-allocation")
@Api(tags = "Service Allocation Controller")
public class ServiceAllocationController {

    private final Logger LOG = LoggerFactory.getLogger(ServicesConfigController.class);
    private final EntityResponse response = new EntityResponse();

    @Autowired
    private ServiceAllocationService allocationService;

    @Autowired
    private ServicesConfigRepository servicesConfigRepository;

    @PostMapping
    public ResponseEntity<?> addService(@RequestBody ServiceAllocation allocation){

        String status = servicesConfigRepository.findAvailabilityStatus(allocation.getServiceId());
        if (Objects.equals(status, "Available")){
            ServiceAllocation service = allocationService.addService(allocation);
            if (service != null){
                response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(service);

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }else {
                response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setEntity(service);

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }else {
            response.setMessage("Service Requested is Unavailable at the moment");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("fetch-all")
    public ResponseEntity<?> fetchAllServiceAllocations(){

        List<ServiceAllocation> allocations = allocationService.fetchAllServiceAllocations();
        if (allocations.size() > 0){
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(allocations);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else if (allocations.size() <= 0){
            response.setMessage("No Record Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setEntity(allocations);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else {
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

}
