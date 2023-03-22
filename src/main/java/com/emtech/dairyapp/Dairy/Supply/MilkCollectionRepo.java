package com.emtech.dairyapp.Dairy.Supply;

import com.emtech.dairyapp.Analytics.AnalyticsData;
import com.emtech.dairyapp.Dairy.Interface.CollectionTracker;
import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.Interface.DailyRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MilkCollectionRepo extends JpaRepository<MilkCollections,Long> {


    List<MilkCollections> findByMember(Long memberId);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.member_code,c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,w.name as ward,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id join farmer f on f.id=c.member join pick_up_locations p on p.id=c.pick_up_location  join ward w on w.id=c.ward_fk where c.collector_id =:collectorId and  DATE(c.collection_date)= :date order by c.collection_date",nativeQuery = true)
    List<CollectionsData> fetchByCollectorandDate(Long collectorId,String date);


    @Query(value = "SELECT f.first_name ,f.last_name ,f.member_code,c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,w.name as ward,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id join farmer f on f.id=c.member join pick_up_locations p on p.id=c.pick_up_location  join ward w on w.id=c.ward_fk where c.collector_id =:collectorId  and DATE(collection_date) BETWEEN :from and :to ",nativeQuery = true)
    List<CollectionsData> getCollectionsByDate(Long collectorId,String from,String to);


    @Query(value = "select * from collections c where c.collector_id = :collectorId order by c.collection_date" ,nativeQuery = true)
    List<CollectionsData> findByCollectorId(Long collectorId);


    @Query(value = "select sum(c.quantity) as quantity,f.float_amount,f.float_spent,f.balance,u.user_name from collections c join float_manager f on c.collector_id= f.collector_id join users u on u.id=c.collector_id group by c.collector_id",nativeQuery = true)
    List<CollectionTracker> getCollectionTracker();

    @Query(value = "SELECT sum(c.quantity) as quantity,u.user_name as username,sum(c.amount) as amount FROM collections c join users u on u.id=c.collector_id WHERE DATE(c.collection_date) = CURDATE() group by c.collector_id order by c.collection_date ",nativeQuery = true)
    List<DailyRecords> getTodaysCollectionsPerCollector();

    @Query(value = "SELECT count(*) as count, sum(c.quantity) as quantity,sum(c.amount) as amount FROM collections c WHERE DATE(c.collection_date) = CURDATE()",nativeQuery = true)
    List<DailyRecords> getTodaysCollections();


    @Query(value = "SELECT f.first_name ,f.last_name ,f.member_code, c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,w.name as ward,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id join farmer f on f.id=c.member join pick_up_locations p on p.id=c.pick_up_location join ward w on w.id=c.ward_fk where DATE(c.collection_date)= :date order by c.collection_date order by c.collection_date",nativeQuery = true)
    List<CollectionsData> getCollectionsbyDate(String date);
    @Query(value = "SELECT f.first_name ,f.last_name ,f.member_code, c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,w.name as ward,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id join farmer f on f.id=c.member join pick_up_locations p on p.id=c.pick_up_location join ward w on w.id=c.ward_fk",nativeQuery = true)
    List<CollectionsData> getAllCollections();
    @Query(value = "SELECT f.first_name ,f.last_name ,f.member_code, c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,w.name as ward,p.name as pickUpLocation  from collections c join users u on c.collector_id =u.id join farmer f on f.id=c.member join pick_up_locations p on p.id=c.pick_up_location join ward w on w.id=c.ward_fk where f.id =:farmerId order by c.collection_date",nativeQuery = true)
    List<CollectionsData> getCollectionsbyFarmer(Long farmerId);
    @Query(value = "SELECT f.first_name ,f.last_name ,f.member_code,c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,w.name as ward,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id join farmer f on f.id=c.member join pick_up_locations p on p.id=c.pick_up_location  join ward w on w.id=c.ward_fk where c.collector_id =:collectorId order by c.collection_date",nativeQuery = true)
    List<CollectionsData> getCollectionsbyCollector(Long collectorId);
    @Query(value = "SELECT f.first_name ,f.last_name ,f.member_code,c.id , c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,w.name as ward,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id join farmer f on f.id=c.member join pick_up_locations p on p.id=c.pick_up_location  join ward w on w.id=c.ward_fk where DATE(c.collection_date) BETWEEN :fromDate and :toDate order by c.collection_date",nativeQuery = true)
    List<CollectionsData> getCollectionByDateRange(String fromDate,String toDate);


    @Query(value = "select  max(id) from collections",nativeQuery = true)
    Long getMaxVaue();


    @Query(value = "SELECT sum(c.amount) as amount,SUM(c.quantity) as quantity ,u.user_name as collector  from collections c join users u on u.id=c.collector_id  WHERE DATE(c.collection_date)=:date  GROUP BY c.collector_id",nativeQuery = true)
    List<AnalyticsData> getCollectorDataPerDate(String date);

    @Query(value = "SELECT sum(c.amount) as amount,SUM(c.quantity) as quantity,MONTHNAME(c.collection_date) as month from collections c where YEAR (c.collection_date)=:year GROUP BY MONTH(c.collection_date)",nativeQuery = true)
    List<AnalyticsData> getCollectorDataPerYear(Integer year);

    @Query(value = "SELECT c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,w.name as ward,p.name as pickUpLocation  from collections c join users u on c.collector_id =u.id join farmer f on f.id=c.member join pick_up_locations p on p.id=c.pick_up_location join ward w on w.id=c.ward_fk where c.collection_number =:collectionCode",nativeQuery = true)
    Optional<CollectionsData> getCollectionsbyCollectionCode(String collectionCode);

    @Query(value = "SELECT sum(c.quantity) as quantity,p.name as location  from collections c join pick_up_locations p on p.id =c.pick_up_location GROUP BY p.name",nativeQuery = true)
    List<AnalyticsData> getQuantityPerLocation();









}
