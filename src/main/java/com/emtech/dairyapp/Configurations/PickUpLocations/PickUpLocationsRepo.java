package com.emtech.dairyapp.Configurations.PickUpLocations;

import com.emtech.dairyapp.Configurations.Interfaces.PickUpLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickUpLocationsRepo extends JpaRepository<PickUpLocations, Long> {

    @Query(value = "select s.name as subcounty,p.id,p.name,w.name as ward,p.land_mark as landmark, count(*) as collectors from collector c join pick_up_locations p on p.id=c.location_id join ward w on w.id=p.ward_fk join subcounty s on s.id=p.subcounty_fk group by p.id", nativeQuery = true)
    List<PickUpLocation> getAllPickUpLocations();


}
