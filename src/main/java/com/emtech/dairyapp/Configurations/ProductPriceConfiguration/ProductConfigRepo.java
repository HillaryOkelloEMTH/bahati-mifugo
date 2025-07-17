package com.emtech.dairyapp.Configurations.ProductPriceConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductConfigRepo extends JpaRepository<ProductConfig,Long> {

    Optional<ProductConfig> findByMccFkAndRouteFk(Long routeId, Long mccFk);

    @Query(value = "select * from product_config where mcc_fk = :mccFk and route_fk is null", nativeQuery = true)
    Optional<ProductConfig> findByMcc(Long mccFk);

    @Query(value = "Select * from product_config p join route r on p.route_fk = r.id where r.location_id = :mccFk and p.route_fk is not null", nativeQuery = true)
    List<ProductConfig> findAllRouteConfigs(Long mccFk);

    @Query(value = "select p.id as id, p.product_name,p.buying_price ,p.selling_price ,p.quantity ,p.unit_measurement ,p.effective_from, p.modified_date as modified_on ,pul.name as mcc from product_config p join pick_up_locations pul on p.mcc_fk =pul.id where p.route_fk is null", nativeQuery = true)
    List<AllProductConfig> findAllMccConfigs();

    @Query(value = "select p.id as id, p.product_name,p.buying_price, r.route, p.selling_price ,p.quantity ,p.unit_measurement ,p.effective_from, p.modified_date as modified_on from product_config p\n" +
            "join route r on p.route_fk = r.id where r.location_id = :mccFk and p.route_fk is not null", nativeQuery = true)
    List<AllProductConfig> findCenterConfigs(Long mccFk);

    @Query(value = "SELECT pc.id as id, pc.product_name,pc.buying_price ,pc.selling_price ,pc.quantity ,pc.unit_measurement ,pc.effective_from ,r.route  from product_config pc join route r on r.id=pc .route_fk",nativeQuery = true)
    List<AllProductConfig> getAllProducConfig();

    @Query(value = "update collections set amount = round(quantity* :newPrice, 2), current_price= :newPrice where date(collection_date) >= :effectiveFrom " +
            "and route_fk in(select id from route where location_id = :locationId);", nativeQuery = true)
    void updateAllMccCollectionPrices(Long locationId, double newPrice, String effectiveFrom);

    @Query(value = "update collections set amount = round(quantity* :newPrice, 2), current_price= :newPrice where date(collection_date) >= :effectiveFrom " +
            "and route_fk = :routeFk", nativeQuery = true)
    void updateRouteCollectionPrices(Long routeFk, double newPrice, String effectiveFrom);

    @Query(value = "SELECT pc.id as id, pc.product_name,pc.buying_price ,pc.selling_price ,pc.quantity ,pc.unit_measurement ,pc.effective_from ,r.route  from product_config pc join route r on r.id=pc .route_fk",nativeQuery = true)
    List<AllProductConfig> getAllMccConfig();

    interface AllProductConfig{
        String getProduct_name();
        Long getId();
        Double getBuying_price();
        Double getSelling_price();
        Integer getQuantity();
        String getUnit_measurement();
        Date getEffective_from();
        String getRoute();
        String getMcc();
        Date getModified_on();
    }


   


}
