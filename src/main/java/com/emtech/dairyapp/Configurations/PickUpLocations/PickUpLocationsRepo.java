package com.emtech.dairyapp.Configurations.PickUpLocations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickUpLocationsRepo extends JpaRepository<PickUpLocations,Long> {
}
