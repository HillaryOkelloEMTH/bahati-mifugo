package com.emtech.dairyapp.Configurations.servicesConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ServicesConfigRepository extends JpaRepository<ServicesConfig, Long> {

}
