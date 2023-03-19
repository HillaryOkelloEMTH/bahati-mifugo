package com.emtech.dairyapp.Configurations.ProductConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductConfigRepo extends JpaRepository<ProductConfig,Long> {

    Optional<ProductConfig> findByProductName(String name);

   


}
