package com.emtech.dairyapp.Configurations.VehicleManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepo extends JpaRepository<Vehicle,Long> {

    Optional<Vehicle> findByAssignedTo(String username);
}
