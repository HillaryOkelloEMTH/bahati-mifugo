package com.emtech.dairyapp.Configurations.FarmerManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmerRepo extends JpaRepository<Farmer,Long> {



    List<Farmer> findByDeletedFlag(Character deletedFlag);
    List<Farmer> findByWardFk(Long wardId);
}
