package com.emtech.dairyapp.Configurations.Routes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepo extends JpaRepository<Route,Long> {

    List<Route> findByDeletedFlag(Character deletedFlag);

    @Query(value = "select r.* from route r join pick_up_locations p on p.subcounty_fk= :sub_countyFk where r.location_id = p.id and r.deleted_flag =:deletedFlag", nativeQuery = true)
    List<Route>findByDeletedFlagAndCenter(Character deletedFlag, Long sub_countyFk);

    @Query(value = "select * from route where active_flag='Y'",nativeQuery = true)
    List<Route> getRoutes();

    @Query(value = "select c.username as username from pick_up_locations p join route r on r.location_id=p.id join collector c on c.location_id=p.id where r.id= :routeId order by c.id desc limit 1", nativeQuery = true)
    String getFarmerCollector(Long routeId);

    @Query(value = "select * from route where location_id = :locationId and active_flag = 'Y'", nativeQuery = true)
    List<Route> findCenterRoutes(Long locationId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT r.route, r.id, p.name as pickUpLocation, w.name as ward, p.land_mark as landmark " +
            "FROM route r " +
            "JOIN pick_up_locations p ON p.id = r.location_id " +
            "LEFT JOIN ward w ON w.id = p.ward_fk " +
            "CROSS JOIN users u " +
            "LEFT JOIN collector c ON p.id = c.location_id AND LOWER(TRIM(c.username)) = LOWER(TRIM(u.user_name)) " +
            "WHERE u.id = :collectorId " +
            "AND (c.id IS NOT NULL OR (u.pick_up_location IS NOT NULL AND (LOWER(TRIM(p.name)) = LOWER(TRIM(u.pick_up_location)) OR CAST(p.id AS CHAR) = TRIM(u.pick_up_location)))) " +
            "AND (r.active_flag = 'Y' OR r.active_flag IS NULL OR r.active_flag = '1') " +
            "AND (r.deleted_flag = 'N' OR r.deleted_flag IS NULL)")
    List<CollectorRoutes> getRouteByCollector(Long collectorId);
    interface CollectorRoutes{
        Long getId();
        String getRoute();
        String getPickUpLocation();
        String getWard();
        String getLandmark();

    }





}
