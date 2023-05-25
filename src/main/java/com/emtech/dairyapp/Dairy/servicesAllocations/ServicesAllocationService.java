package com.emtech.dairyapp.Dairy.servicesAllocations;

import com.emtech.dairyapp.Dairy.Interface.Allocations;
import com.emtech.dairyapp.Dairy.Interface.Services;
import com.emtech.dairyapp.Dairy.ProductAllocations.FarmerProdAllocattionsRepo;
import com.emtech.dairyapp.Dairy.ProductAllocations.FarmerProductAllocations;
import com.emtech.dairyapp.Dairy.ProductAllocations.dto.ServiceApplicationUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    public List<Services> fetchAllServicesHistoryApplications() {
        try {
            return farmerProdAllocattionsRepo.fetchAllServicesHistoryApplications();
        }catch (Exception exc){
            LOG.error("Error While Fetching Allocations ::: {}", exc.getLocalizedMessage());
            return null;
        }
    }

    public List<Services> fetchAllServicesHistoryApplicationsByFarmerNo(Long memberNo) {
        try {
            return farmerProdAllocattionsRepo.fetchAllServicesHistoryApplicationsByFarmerNo(memberNo);
        }catch (Exception exc){
            LOG.error("Error While Fetching Allocations ::: {}", exc.getLocalizedMessage());
            return null;
        }
    }

    public List<Services> fetchAllOpenServicesApplicationsByFarmerNo(Long memberNo) {
        try {
            return farmerProdAllocattionsRepo.fetchAllOpenServicesApplicationsByFarmerNo(memberNo);
        }catch (Exception exc){
            LOG.error("Error While Fetching Allocations ::: {}", exc.getLocalizedMessage());
            return null;
        }
    }

    public FarmerProductAllocations updateServiceDetails(ServiceApplicationUpdate applicationUpdate) {
        try {
            Optional<FarmerProductAllocations> allocationCheck = farmerProdAllocattionsRepo.findById(applicationUpdate.getServiceId());
            if (allocationCheck.isPresent()){
                FarmerProductAllocations allocation = allocationCheck.get();
                allocation.setResolvedOn(new Date());
                allocation.setServiceStatus(applicationUpdate.getStatus());
                allocation.setResolvedBy(applicationUpdate.getResolvedBy());
                allocation.setComments(applicationUpdate.getComments());
                return farmerProdAllocattionsRepo.save(allocation);
            }else {
                LOG.info("Service with service id {} provided does not exist", applicationUpdate.getServiceId());
                return null;
            }

        }catch (Exception exc){
            LOG.error("Error encountered while updating service Details :::: {}", exc.getLocalizedMessage());
            return null;
        }
    }
}


