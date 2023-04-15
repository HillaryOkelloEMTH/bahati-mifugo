package com.emtech.dairyapp.Dairy.servicesAllocations;


import com.emtech.dairyapp.Configurations.servicesConfig.ServicesConfig;
import com.emtech.dairyapp.Configurations.servicesConfig.ServicesConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    public List<ServiceAllocation> fetchServicingStatus(String status) {
        try {
            LOG.error("FETCHING ALL SERVICES BY SERVICING STATUS ::: {}", status);
            return allocationRepository.findAllByServicingStatus(status);
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE FETCHING ALL SERVICES BY SERVICING STATUS ::: {}", exc.getLocalizedMessage());
            return null;
        }
    }

    public Optional<ServiceAllocation> fetchServiceAllocationById(Long id) {
        try {
            LOG.error("FETCHING SERVICE ALLOCATION WITH ID ::: {}", id);
            return allocationRepository.findById(id);
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE FETCHING ALLOCATION SERVICE ::: {}", exc.getLocalizedMessage());
            return Optional.empty();
        }
    }

    public ServiceAllocation updateServiceAllocation(ServiceAllocation serviceAllocation) {
        try {
            LOG.error("UPDATING SERVICE ALLOCATION ::: {}", serviceAllocation.toString());

            Double amount = servicesConfigRepository.findServiceAmountByid(serviceAllocation.getServiceId());
            serviceAllocation.setAmount(amount);
            serviceAllocation.setUpdatedOn(new Date());
            serviceAllocation.setPaymentStatus(serviceAllocation.getPaymentStatus());
            ServiceAllocation allocation = allocationRepository.save(serviceAllocation);

            return allocation;
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE UPDATING SERVICE ALLOCATION ::: {}", exc.getLocalizedMessage());
            return null;
        }
    }

    public void deleteServiceAllocation(Long id) {
        try {
            LOG.error("DELETING ALLOCATION SERVICE WITH ID ::: {}", id);
            allocationRepository.deleteById(id);
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE DELETING ALLOCATION SERVICE ::: {}", exc.getLocalizedMessage());
        }
    }
}
