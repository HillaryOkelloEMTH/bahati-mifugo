package com.emtech.dairyapp.Dairy.FloatTracking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FloatManagerRepo extends JpaRepository<FloatManager,Long> {


    Optional<FloatManager> findByCollectorId(Long id);

    @Query(value = "select f.id,f.collector_id,u.user_name,f.balance,f.float_amount,f.float_spent,f.allocated_by from float_manager f join users u on u.id=f.collector_id",nativeQuery = true)
    List<FloatData> getAllFloatManager();


    interface  FloatData{
        Long getId();
        Long getCollector_id();
        String getUser_name();
        String getAllocated_by();
        Double getBalance();
        String getFloat_amount();
        String getFloat_spent();

    }
}
