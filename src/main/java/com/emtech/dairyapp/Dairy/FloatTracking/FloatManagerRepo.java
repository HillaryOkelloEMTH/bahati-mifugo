package com.emtech.dairyapp.Dairy.FloatTracking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloatManagerRepo extends JpaRepository<FloatManager,Long> {


    FloatManager findByCollectorId(Long id);
}
