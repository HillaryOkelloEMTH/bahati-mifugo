package com.emtech.dairyapp.Configurations.MccProductPrices;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {


    @Query(value = "select pp.id as priceId, pp.buying_price, pp.selling_price, pp.effective_from, c.name as mcc, p.name as productName, p.category from product_price pp join pick_up_locations c on pp.location_id=c.id join product p on pp.product_id=p.id", nativeQuery = true)
    List<ProductPriceInterface> getMccProductPrices();


    boolean existsByProductIdAndLocationId(Long productId, Long locationId);

    Optional<ProductPrice> findByProductIdAndLocationId(Long productId, Long locationId);


    Optional<ProductPrice> findByProductId(Long productId);

    public interface ProductPriceInterface {
        Long getPriceId();
        String getMcc();
        double getBuying_price();
        double getSelling_price();
        Date getEffective_from();
        String getProductName();
        String getCategory();
    }
}
