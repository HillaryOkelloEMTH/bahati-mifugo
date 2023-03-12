package com.emtech.dairyapp.Configurations.SubCounty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubcountyRepo extends JpaRepository<Subcounty,Long> {


}
