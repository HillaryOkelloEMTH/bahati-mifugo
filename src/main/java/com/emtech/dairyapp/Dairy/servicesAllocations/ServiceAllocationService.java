package com.emtech.dairyapp.Dairy.servicesAllocations;


import com.emtech.dairyapp.Configurations.servicesConfig.ServicesConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ServiceAllocationService {

    private final Logger LOG = LoggerFactory.getLogger(ServiceAllocationService.class);

    @Autowired
    private ServiceAllocationRepository allocationRepository;
    @Autowired
    private ServicesConfigRepository servicesConfigRepository;


    public ServiceAllocation addService(ServiceAllocation allocation) {
        try {
            LOG.error("ALLOCATING SERVICE ::: {}", allocation.toString());

            Double amount = servicesConfigRepository.findServiceAmountByid(allocation.getServiceId());
            allocation.setAmount(amount);
            allocation.setRequestedOn(new Date());
            allocation.setPaymentStatus("N");
            ServiceAllocation serviceAllocation = allocationRepository.save(allocation);

            return serviceAllocation;
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE ALLOCATING SERVICE::: {}", exc.getLocalizedMessage());
            return null;
        }
    }

    public List<ServiceAllocation> fetchAllServiceAllocations() {
        try {
            LOG.error("FETCHING ALL SERVICE ALLOCATIONS ::: ");
            return allocationRepository.findAll();
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE FETCHING ALL SERVICE ALLOCATIONS ::: {}", exc.getLocalizedMessage());
            return null;
        }
    }
}
