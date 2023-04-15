package com.emtech.dairyapp.Configurations.servicesConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ServicesConfigRepository extends JpaRepository<ServicesConfig, Long> {

    List<ServicesConfig> findAllByStatus(String status);

    ServicesConfig findByIdAndStatus(Long id, String status);

    @Query(nativeQuery = true, value = "select status from services where id= :id")
    String findAvailabilityStatus(Long id);

    List<ServicesConfig> findAllByServicingStatus(String status);

    @Query(nativeQuery = true, value = "select service_amount from services where id= :id")
    Double findServiceAmountByid(Long id);



}
