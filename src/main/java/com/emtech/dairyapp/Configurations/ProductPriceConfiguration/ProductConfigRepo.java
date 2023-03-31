package com.emtech.dairyapp.Configurations.ProductPriceConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductConfigRepo extends JpaRepository<ProductConfig,Long> {

    Optional<ProductConfig> findByProductName(String name);

   


}
