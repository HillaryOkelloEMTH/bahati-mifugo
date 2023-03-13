package com.emtech.dairyapp.Configurations.SubCounty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcountyRepo extends JpaRepository<Subcounty,Long> {

    List<Subcounty> findByCountyFk(Long county_fk);


}
