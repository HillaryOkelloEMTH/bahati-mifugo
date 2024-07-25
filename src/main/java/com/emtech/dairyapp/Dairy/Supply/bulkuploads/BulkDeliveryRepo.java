package com.emtech.dairyapp.Dairy.Supply.bulkuploads;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulkDeliveryRepo extends JpaRepository<BulkDelivery, Long> {
}
