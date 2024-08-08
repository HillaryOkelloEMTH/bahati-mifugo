package com.emtech.dairyapp.Configurations.PickUpLocations;

import com.emtech.dairyapp.Configurations.Interfaces.Locations;
import com.emtech.dairyapp.Configurations.Interfaces.PickUpLocation;
import com.emtech.dairyapp.Configurations.Interfaces.PickUpPoints;
import com.emtech.dairyapp.Configurations.Routes.Route;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickUpLocationsRepo extends JpaRepository<PickUpLocations, Long> {

    boolean existsById(@NotNull Long id);

    @Query(value = "select s.name as subcounty,p.id,p.name,w.name as ward,p.land_mark as landmark, count(*) as collectors from collector c join pick_up_locations p on p.id=c.location_id join ward w on w.id=p.ward_fk join subcounty s on s.id=p.subcounty_fk group by p.id", nativeQuery = true)
    List<PickUpLocation> getAllPickUpLocations();

    @Query(value = "select p.name, p.land_mark as landmark from pick_up_locations p join collector c on p.id=c.location_id where c.username= :username and p.ward_fk= :wardId",nativeQuery = true)
    List<PickUpPoints> getPickUpLocations(String username, Long wardId);

    @Query(value = "SELECT p.id,p.name ,p.land_mark as landmark,w.name as ward from pick_up_locations p join ward w on w.id =p.ward_fk  join collector c ON p.id =c.location_id join users u on c.username =u.user_name WHERE u.id = :collectorId and w.id = :ward_fk",nativeQuery = true)
    List<Locations> getPickUpLcoationsBywardandCollectorId(Long collectorId, Long ward_fk );
    @Query(value = "SELECT p.id,p.name ,p.land_mark as landmark,w.name as ward from pick_up_locations p join ward w on w.id =p.ward_fk  join collector c ON p.id =c.location_id join users u on c.username =u.user_name WHERE u.id = :collectorId",nativeQuery = true)
    List<Locations> getPickUpLcoationsByCollectorId(Long collectorId );

    @Query(value = "SELECT p.id,p.name ,p.land_mark as landmark,w.name as ward from pick_up_locations p join ward w on w.id =p.ward_fk join route r ON p.id =r.location_id join transporter t on t.route_id = r.id join users u on u.user_name = t.username  WHERE u.id = :transporterId", nativeQuery = true)
    List<Locations> getTransporterLocations(Long transporterId);

    @Query(value = "SELECT r.id,r.route  from route r  WHERE r.location_id= :locationId",nativeQuery = true)
    List<RouteInterface> getRoutesPerLocation(Long locationId );


    interface RouteInterface{
        Long getId();
        String getRoute();
    }

    @Query(value = "update users set pick_up_location=:location WHERE  user_name=:username",nativeQuery = true)
    void  updateCollectorInformation(String location,String username);

}
