package com.emtech.dairyapp.Stock.MccAllocations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MccAllocationRepo extends JpaRepository<MccAllocation, Long> {


    @Query(value = "select ma.product_id, ma.allocated_on, ma.stock, p.name, p.description, p.category, pp.selling_price from mcc_allocation ma join product p on ma.product_id=p.id join product_price pp on ma.product_id=pp.product_id where ma.location_id = :locationId", nativeQuery = true)
    List<MccProducts> getMcProducts(Long locationId);

    public interface MccProducts {
        Long getProduct_id();
        Date getAllocated_on();
        Integer getStock();
        String getName();
        String getDescription();
        String getCategory();
        Double getSelling_price();
    }
}
