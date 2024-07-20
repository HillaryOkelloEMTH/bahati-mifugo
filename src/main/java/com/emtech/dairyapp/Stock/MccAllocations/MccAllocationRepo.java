package com.emtech.dairyapp.Stock.MccAllocations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MccAllocationRepo extends JpaRepository<MccAllocation, Long> {


    Optional<MccAllocation> findByProductIdAndLocationId(Long productId, Long locationId);


    @Query(value = "select ma.product_id, ma.allocated_on, ma.stock, p.name, p.description, p.category, p.type, p.category_id, pp.selling_price from mcc_allocation ma join product p on ma.product_id=p.id join product_price pp on ma.product_id=pp.product_id where ma.location_id = :locationId  group by ma.id", nativeQuery = true)
    List<MccProducts> getMccProducts(Long locationId);

    @Query(value = "select ma.product_id, ma.allocated_on, ma.stock, p.name, mcc.name as mcc, p.description, p.category, p.type, p.category_id,p.price, pp.selling_price from mcc_allocation ma join product p on ma.product_id=p.id join product_price pp on ma.product_id=pp.product_id join pick_up_locations mcc on ma.location_id=mcc.id group by ma.id", nativeQuery = true)
    List<MccProducts> getAllMccProducts();

    public interface MccProducts {
        Long getProduct_id();
        Date getAllocated_on();
        Integer getStock();
        String getName();
        String getType();
        String getDescription();
        String getCategory();
        Long getCategory_id();
        Double getSelling_price();
        Double getPrice();
        String getMcc();
    }
}
