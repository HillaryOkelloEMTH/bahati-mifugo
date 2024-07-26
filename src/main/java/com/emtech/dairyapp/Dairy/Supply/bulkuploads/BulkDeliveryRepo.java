package com.emtech.dairyapp.Dairy.Supply.bulkuploads;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulkDeliveryRepo extends JpaRepository<BulkDelivery, Long> {

    @Query(value = "select * from bulk_delivery where date(posted_on) between :from and :to", nativeQuery = true)
    List<BulkDelivery> getUploadsByDate(String from, String to);
}
