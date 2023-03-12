package com.emtech.dairyapp.Configurations.SubCounty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardRepo extends JpaRepository<Ward,Long> {

    @Query(value = "select * from ward where subcounty_id= :subcounty_id",nativeQuery = true)
    List<Ward> findBySubcounty(Long subcounty_id);
}
