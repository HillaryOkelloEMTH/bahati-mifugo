package com.emtech.dairyapp.Notifications.SMS.smsv1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmsNotificationRepo extends JpaRepository<SMSNotifications,Long> {


    Optional<SMSNotifications> findByMessageId(String messageId);

    List<SMSNotifications> findByCategory(String category);
    List<SMSNotifications> findByBulkCode(String bulkCode);

    @Query(value = "select * from smsnotifications where date(sent_date) between :from and :to order by id desc", nativeQuery = true)
    List<SMSNotifications> findByDateRange(String from, String to);

    @Query(value = "SELECT * FROM smsnotifications WHERE category = 'Bulk' group by bulk_code order by id DESC", nativeQuery = true)
    List<SMSNotifications> getBulkSMSCodes();
}
