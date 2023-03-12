package com.emtech.dairyapp.Configurations.County;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountyRepo extends JpaRepository<County,Long> {
}
