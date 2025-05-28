package com.emtech.dairyapp.Stock.MccAllocations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

//By DateRange, ProductId, LocationId.
    @Query("""
           SELECT   m FROM MccAllocation m
           WHERE (:locationId IS NULL OR m.locationId = :locationId)
             AND (:productId  IS NULL OR m.productId  = :productId)
             AND (:startDate  IS NULL OR m.allocatedOn >= :startDate)
             AND (:endDate    IS NULL OR m.allocatedOn <= :endDate)
           """)
    List<MccAllocation> findAllByFilters(@Param("locationId") Long locationId,
                                         @Param("productId") Long productId,
                                         @Param("startDate") Date startDate,
                                         @Param("endDate") Date endDate,
                                         @Param("month") Integer month,
                                         @Param("year") Integer year
     );
//By Month
    @Query("SELECT m FROM MccAllocation m WHERE FUNCTION('MONTH', m.allocatedOn) = :month AND (:locationId IS NULL OR m.locationId = :locationId) AND (:productId IS NULL OR m.productId = :productId)")
    List<MccAllocation> findAllByMonth(@Param("month") int month, @Param("locationId") Long locationId, @Param("productId") Long productId);
//By Year
    @Query("SELECT m FROM MccAllocation m WHERE FUNCTION('YEAR', m.allocatedOn) = :year AND (:locationId IS NULL OR m.locationId = :locationId) AND (:productId IS NULL OR m.productId = :productId)")
    List<MccAllocation> findAllByYear(@Param("year") int year, @Param("locationId") Long locationId, @Param("productId") Long productId);
//By Month, year, locationId and productId.
    @Query("SELECT m FROM MccAllocation m WHERE FUNCTION('MONTH', m.allocatedOn) = :month AND FUNCTION('YEAR', m.allocatedOn) = :year AND (:locationId IS NULL OR m.locationId = :locationId) AND (:productId IS NULL OR m.productId = :productId)")
    List<MccAllocation> findAllByMonthAndYear(@Param("month") int month,
                                              @Param("year") int year,
                                              @Param("locationId") Long locationId,
                                              @Param("productId") Long productId);


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
