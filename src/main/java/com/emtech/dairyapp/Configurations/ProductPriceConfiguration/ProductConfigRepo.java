package com.emtech.dairyapp.Configurations.ProductPriceConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductConfigRepo extends JpaRepository<ProductConfig,Long> {

    Optional<ProductConfig> findByRouteFk(Long routeId);

    @Query(value = "SELECT pc.id as id, pc.product_name,pc.buying_price ,pc.selling_price ,pc.quantity ,pc.unit_measurement ,pc.effective_from ,r.route  from product_config pc join route r on r.id=pc .route_fk",nativeQuery = true)
    List<AllProductConfig> getAllProducConfig();

    interface AllProductConfig{
        String getProduct_name();
        Long getId();
        Double getBuying_price();
        Double getSelling_price();
        Integer getQuantity();
        String getUnit_measurement();
        Date getEffective_from();
        String getRoute();



    }


   


}
