package com.emtech.dairyapp.Configurations.Routes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepo extends JpaRepository<Route,Long> {

    List<Route>findByDeletedFlag(Character deletedFlag);

    @Query(value = "select * from route",nativeQuery = true)
    List<Route> getRoutes();

    @Query(nativeQuery = true,value = "SELECT r.route,p.name as pickUpLocation from route r join pick_up_locations p on p.id =r.location_id join collector c  on p.id =c.location_id  join users u on u.user_name =c.username where u.id =:collectorId")
    List<CollectorRoutes> getRouteByCollector(Long collectorId);
    interface CollectorRoutes{
        String getRoute();
        String getPickUpLocation();
    }





}
