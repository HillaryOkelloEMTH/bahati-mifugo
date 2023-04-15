package com.emtech.dairyapp.Dairy.servicesAllocations;

import com.emtech.dairyapp.Configurations.servicesConfig.ServicesConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceAllocationRepository extends JpaRepository<ServiceAllocation, Long> {

    List<ServiceAllocation> findAllByServicingStatus(String status);

}
