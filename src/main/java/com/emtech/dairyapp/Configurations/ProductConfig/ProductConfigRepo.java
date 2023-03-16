package com.emtech.dairyapp.Configurations.ProductConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductConfigRepo extends JpaRepository<ProductConfig,Long> {

    ProductConfig findByProductName(String name);

}
