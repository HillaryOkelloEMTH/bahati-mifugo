package com.emtech.dairyapp.Dairy.servicesAllocations;

import com.emtech.dairyapp.Dairy.Interface.Allocations;
import com.emtech.dairyapp.Dairy.Interface.Services;
import com.emtech.dairyapp.Dairy.ProductAllocations.FarmerProdAllocattionsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicesAllocationService {

    private final Logger LOG = LoggerFactory.getLogger(ServicesAllocationService.class);

    @Autowired
    private FarmerProdAllocattionsRepo farmerProdAllocattionsRepo;


    public List<Services> fetchAllServices() {

        try {
            return farmerProdAllocattionsRepo.getAllServiceApplications();
        }catch (Exception exc){
            LOG.error("Error While Fetching Allocations ::: {}", exc.getLocalizedMessage());
            return null;
        }

    }

    public List<Services> fetchAllServicesByServiceStatus(String status) {
        try {
            return farmerProdAllocattionsRepo.getAllServicesByServiceStatus(status);
        }catch (Exception exc){
            LOG.error("Error While Fetching Allocations ::: {}", exc.getLocalizedMessage());
            return null;
        }
    }
}


