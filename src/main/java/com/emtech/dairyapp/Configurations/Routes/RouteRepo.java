package com.emtech.dairyapp.Configurations.Routes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepo extends JpaRepository<Route,Long> {

    List<Route>findByDeletedFlag(Character deletedFlag);

    @Query(value = "select * from route",nativeQuery = true)
    List<Route> getRoutes();



}
