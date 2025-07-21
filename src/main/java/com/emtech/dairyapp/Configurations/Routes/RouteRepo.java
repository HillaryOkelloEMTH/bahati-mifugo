package com.emtech.dairyapp.Configurations.Routes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepo extends JpaRepository<Route,Long> {

    List<Route>findByDeletedFlag(Character deletedFlag);

    @Query(value = "select * from route where active_flag='Y'",nativeQuery = true)
    List<Route> getRoutes();

    @Query(value = "select c.username as username from pick_up_locations p join route r on r.location_id=p.id join collector c on c.location_id=p.id where r.id= :routeId order by c.id desc limit 1", nativeQuery = true)
    String getFarmerCollector(Long routeId);

    @Query(value = "select * from route where location_id = :locationId and active_flag = 'Y'", nativeQuery = true)
    List<Route> findCenterRoutes(Long locationId);

    @Query(nativeQuery = true,value = "SELECT r.route, r.id, p.name as pickUpLocation,w.name as ward,p.land_mark as landmark from route r join pick_up_locations p on p.id =r.location_id join ward w on w.id =p.ward_fk  join collector c  on p.id =c.location_id  join users u on u.user_name =c.username where u.id =:collectorId and r.active_flag= 'Y'")
    List<CollectorRoutes> getRouteByCollector(Long collectorId);
    interface CollectorRoutes{
        Long getId();
        String getRoute();
        String getPickUpLocation();
        String getWard();
        String getLandmark();

    }





}
