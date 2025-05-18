package com.emtech.dairyapp.Configurations.Transporter;

import com.emtech.dairyapp.Configurations.Interfaces.Transporters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransporterRepository extends JpaRepository<Transporter, Long> {
    boolean existsByUsername(String username);

    @Query(nativeQuery = true,value = "SELECT r.route, r.id, p.name as pickUpLocation,w.name as ward,p.land_mark as landmark from route r join pick_up_locations p on p.id =r.location_id join ward w on w.id =p.ward_fk  join transporter t  on r.id =t.route_id  join users u on u.user_name =t.username where u.id =:transporterId")
    List<TransporterRoutes> getTransporterRoutes(Long transporterId);
    @Query(value = "select t.id, t.username, t.active, date(t.created_on) as createdOn, r.route from transporter t join route r on t.route_id=r.id", nativeQuery = true)
    List<Transporters> getTransporters();
    interface TransporterRoutes{
        Long getId();
        String getRoute();
        String getPickUpLocation();
        String getWard();
        String getLandmark();
    }
}
