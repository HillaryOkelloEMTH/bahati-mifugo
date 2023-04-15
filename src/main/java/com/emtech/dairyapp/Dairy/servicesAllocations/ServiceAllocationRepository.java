package com.emtech.dairyapp.Dairy.servicesAllocations;

import com.emtech.dairyapp.Configurations.servicesConfig.ServicesConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceAllocationRepository extends JpaRepository<ServiceAllocation, Long> {

    List<ServiceAllocation> findAllByServicingStatus(String status);

    @Query(nativeQuery = true, value = "select * from service_allocation where farmerno= :farmerno")
    List<ServiceAllocation> fetchFarmerAllServiceAllocations(Long farmerno);
}
