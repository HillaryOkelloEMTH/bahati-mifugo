package com.emtech.dairyapp.Configurations.SubCounty;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcountyRepo extends JpaRepository<Subcounty,Long> {

    List<Subcounty> findByCountyFk(Long county_fk);

    @Query(value = "select sc.id, c.name as county, sc.name as subcounty,count(*)  as wardCount from ward w join subcounty sc on sc.id=w.subcounty_id join county c on c.id=sc.county_fk group by sc.id",nativeQuery = true)
    List<com.emtech.dairyapp.Configurations.Interfaces.Subcounty> selectAll();


    Boolean existsByName(String name);


}
