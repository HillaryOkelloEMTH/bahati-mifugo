package com.emtech.dairyapp.Dairy.FloatTracking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FloatManagerRepo extends JpaRepository<FloatManager,Long> {


    Optional<FloatManager> findByCollectorId(Long id);
}
