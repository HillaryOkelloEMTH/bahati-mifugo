package com.emtech.dairyapp.Dairy.servicesAllocations;


import com.emtech.dairyapp.Configurations.servicesConfig.ServicesConfigController;
import com.emtech.dairyapp.Response.EntityResponse;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/service-allocation")
@Api(tags = "Service Allocation Controller")
public class ServiceAllocationController {

    private final Logger LOG = LoggerFactory.getLogger(ServicesConfigController.class);
    private final EntityResponse response = new EntityResponse();



}
