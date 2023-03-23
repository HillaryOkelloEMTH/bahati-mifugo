package com.emtech.dairyapp.Configurations.CanManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CanRepo extends JpaRepository<Can,Long> {

    @Query(value = "select count(*) from can",nativeQuery = true)
    Integer getAllCans();
    @Query(value = "select max(id) from can",nativeQuery = true)
    Integer getMaxValue();

    Optional<Can> findByCanNo(String canNumber);
}
