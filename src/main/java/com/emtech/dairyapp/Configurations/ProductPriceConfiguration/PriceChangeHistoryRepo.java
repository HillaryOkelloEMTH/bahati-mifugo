package com.emtech.dairyapp.Configurations.ProductPriceConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceChangeHistoryRepo extends JpaRepository<PriceChangeHistory,Long> {


    List<PriceChangeHistory> findByProductConfigId(Long productConfigId);
}
