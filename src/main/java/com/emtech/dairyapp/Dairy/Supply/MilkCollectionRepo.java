package com.emtech.dairyapp.Dairy.Supply;

import com.emtech.dairyapp.Analytics.AnalyticsData;
import com.emtech.dairyapp.Dairy.Interface.*;
import com.emtech.dairyapp.Dairy.PaymentComponent.PaymentFileData;
import com.emtech.dairyapp.Reports.FarmerDetails;
import com.emtech.dairyapp.Reports.FarmerStmtDetails;
import com.emtech.dairyapp.Reports.ReportData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MilkCollectionRepo extends JpaRepository<MilkCollections, Long> {


    List<MilkCollections> findByFarmerNo(Integer farmer_noId);
    Optional<MilkCollections> findByCollectionNumber(String deliveryNo);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.updated_status as updateStatus,c.can_no as canNo,c.original_quantity as originalQuantity,c.id, c.session, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk  where c.collector_id =:collectorId and c.event= 'Collection' and DATE(c.collection_date)= :date order by c.collection_date", nativeQuery = true)
    List<CollectionsData> fetchByCollectorandDate(Long collectorId, String date);


    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.updated_status as updateStatus,c.can_no as canNo,c.original_quantity as originalQuantity,c.id, c.session, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk  where c.collector_id =:collectorId and c.session like :session and c.farmer_no = :farmerNo and c.event= 'Collection' and DATE(c.collection_date)= :date order by c.collection_date", nativeQuery = true)
    List<CollectionsData> filterTodaysCollections(Long collectorId, String date, String farmerNo, String session);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.updated_status as updateStatus,c.can_no as canNo,c.original_quantity as originalQuantity,c.id, c.session, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk  where c.collector_id =:collectorId and c.session = :session  and c.event= 'Collection' and DATE(c.collection_date)= :date order by c.collection_date", nativeQuery = true)
    List<CollectionsData> filterTodaysCollectionsBySession(Long collectorId, String date, String session);

    @Query(value = "SELECT c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,c.phone_no,u.user_name as collector,c.amount,c.quantity,c.collection_date from collections c join users u on c.collector_id =u.id  where c.collector_id =:collectorId and c.event= 'Buying' and DATE(c.collection_date) BETWEEN :from AND :to order by c.collection_date", nativeQuery = true)
    List<PurchaseData> getCollectorsPurchasesByDateRange(Long collectorId, String from,String to);
    @Query(value = "SELECT c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,c.phone_no,u.user_name as collector,c.amount,c.quantity,c.collection_date from collections c join users u on c.collector_id =u.id  where c.collector_id =:collectorId and c.event= 'Buying' and DATE(c.collection_date)= :date order by c.collection_date", nativeQuery = true)
    List<PurchaseData> getCollectorsPurchasesByDate(Long collectorId, String date);


    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.can_no as canNo,c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk   where c.collector_id =:collectorId and c.event= 'Collection'  and DATE(collection_date) BETWEEN :from and :to ", nativeQuery = true)
    List<CollectionsData> getCollectionsByDate(Long collectorId, String from, String to);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.updated_status as updateStatus,c.can_no as canNo,c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date, c.session ,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk   where c.collector_id =:collectorId and c.farmer_no like :farmerNo and c.session like :session and c.event= 'Collection'  and DATE(collection_date) BETWEEN :from and :to ", nativeQuery = true)
    List<CollectionsData> getFilteredCollections(Long collectorId, String farmerNo,  String session,  String from, String to);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.updated_status as updateStatus,c.can_no as canNo,c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk  where c.collector_id =:collectorId and c.payment_status =:paymentStatus and c.event= 'Buying' and DATE(collection_date) BETWEEN :from and :to ", nativeQuery = true)
    List<CollectionsData> fetchCollectorsCollectionsHistoryByDateRangeAndPaymentStatus(Long collectorId, String from, String to, Character paymentStatus);


    @Query(value = "select * from collections c where c.collector_id = :collectorId order by c.collection_date", nativeQuery = true)
    List<CollectionsData> findByCollectorId(Long collectorId);


    @Query(value = "select sum(c.quantity) as quantity,f.float_amount,f.float_spent,f.balance,u.user_name from collections c join float_manager f on c.collector_id= f.collector_id join users u on u.id=c.collector_id group by c.collector_id", nativeQuery = true)
    List<CollectionTracker> getCollectionTracker();

    @Query(value = "SELECT sum(c.quantity) as quantity,u.user_name as username,sum(c.amount) as amount FROM collections c join users u on u.id=c.collector_id WHERE DATE(c.collection_date) = CURDATE() and c.event ='Collection' group by c.collector_id order by c.collection_date ", nativeQuery = true)
    List<DailyRecords> getTodaysCollectionsPerCollector();

    @Query(value = "SELECT count(*) as count,COALESCE(ROUND(SUM(c.quantity),2),0.0)  as quantity,COALESCE(ROUND(SUM(c.amount),2),0.0) as amount FROM collections c WHERE DATE(c.collection_date) = CURDATE() and c.event ='Collection'", nativeQuery = true)
    List<DailyRecords> getTodaysCollections();

    @Query(value = "SELECT count(*) as count,COALESCE(ROUND(SUM(c.quantity),2),0.0)  as quantity,COALESCE(ROUND(SUM(c.amount),2),0.0) as amount FROM collections c WHERE DATE(c.collection_date) = :date and c.event ='Collection'", nativeQuery = true)
    List<DailyRecords> getSpecificDateRecord(String date);
    @Query(value = "SELECT count(*) as count,COALESCE(ROUND(SUM(c.quantity),2),0.0)  as quantity,COALESCE(ROUND(SUM(c.amount),2),0.0) as amount FROM collections c WHERE DATE(c.collection_date) between :from and :to and c.event ='Collection'", nativeQuery = true)
    List<DailyRecords> getDateRangeRecord(String from,String to);
    @Query(value = "SELECT count(*) as count,COALESCE(ROUND(SUM(c.quantity),2),0.0)  as quantity,COALESCE(ROUND(SUM(c.amount),2),0.0) as amount FROM collections c join route r on r.id=c.route_fk join pick_up_locations p on p.id =r.location_id where p.id =:locationid and  c.event ='Collection'", nativeQuery = true)
    List<DailyRecords> getPickUpLocationRecord(Long locationid);
    @Query(value = "SELECT count(*) as count,COALESCE(ROUND(SUM(c.quantity),2),0.0)  as quantity,COALESCE(ROUND(SUM(c.amount),2),0.0) as amount FROM collections c join route r on r.id=c.route_fk  where r.id =:routeId and  c.event ='Collection'", nativeQuery = true)
    List<DailyRecords> getRouteRecord(Long routeId);

    @Query(value = "SELECT r.route as route, count(*) as count,COALESCE(ROUND(SUM(c.quantity),2),0.0)  as quantity,COALESCE(ROUND(SUM(c.amount),2),0.0) as amount FROM collections c join route r on r.id=c.route_fk where  c.event ='Collection' group by c.route_fk",nativeQuery = true)
    List<DailyRecords> getRouteSummary();
    @Query(value = "SELECT count(*) as count,COALESCE(ROUND(SUM(c.quantity),2),0.0)  as quantity,COALESCE(ROUND(SUM(c.amount),2),0.0) as amount FROM collections c where c.event ='Collection'", nativeQuery = true)
    List<DailyRecords> getAllColectionsRecord();

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no ,c.updated_status as updateStatus,c.can_no as canNo,c.original_quantity as originalQuantity,c.session, c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,r.route as route ,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk join pick_up_locations p on p.id =r.location_id where DATE(c.collection_date) =:date order by c.collection_date", nativeQuery = true)
    List<CollectionsData> getCollectionsbyDate(String date);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no ,c.updated_status as updateStatus,c.can_no as canNo,c.original_quantity as originalQuantity,c.session, c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,r.route as route,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk join pick_up_locations p on p.id =r.location_id", nativeQuery = true)
    List<CollectionsData> getAllCollections();


    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no ,c.updated_status as updateStatus,c.can_no as canNo,c.original_quantity as originalQuantity,c.session, c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,f.mobile_no, c.can_no, c.session, c.latitude, c.longitude, c.route_fk, r.route as route,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk join pick_up_locations p on p.id =r.location_id where c.id= :id", nativeQuery = true)
    CollectionItemData getCollectionDetailsByCollectionId(Long id);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.updated_status as updateStatus,c.can_no as canNo,c.original_quantity as originalQuantity,c.session, c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,r.route as route,p.name as pickUpLocation  from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk join pick_up_locations p on p.id =r.location_id where f.id =:farmerId order by c.collection_date", nativeQuery = true)
    List<CollectionsData> getCollectionsbyFarmer(Long farmerId);
    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.can_no as canNo,c.original_quantity as originalQuantity,c.session, c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,r.route as route,p.name as pickUpLocation  from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk join pick_up_locations p on p.id =r.location_id where DATE(c.collection_date)=:date and p.id =:locationid order by c.collection_date",nativeQuery = true)
    List<CollectionsData> getCollectionsbyPickUpLocationAndDate(Long locationid,String date);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.updated_status as updateStatus,c.can_no as canNo,c.original_quantity as originalQuantity,c.session, c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,r.route as route,p.name as pickUpLocation  from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk join pick_up_locations p on p.id =r.location_id where p.id =:locationid order by c.collection_date",nativeQuery = true)
    List<CollectionsData> getCollectionsbyPickUpLocation(Long locationid);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.updated_status as updateStatus,c.can_no as canNo,c.original_quantity as originalQuantity,c.session, c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,r.route as route,p.name as pickUpLocation  from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk join pick_up_locations p on p.id =r.location_id where r.id =:routeId order by c.collection_date",nativeQuery = true)
    List<CollectionsData> getCollectionsbyRoute(Long routeId);



    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.updated_status as updateStatus,c.can_no as canNo,c.original_quantity as originalQuantity,c.session,c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,r.route as route,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk join pick_up_locations p on p.id =r.location_id  where c.collector_id =:collectorId order by c.collection_date", nativeQuery = true)
    List<CollectionsData> getCollectionsbyCollector(Long collectorId);

    @Query(value = "SELECT c.session,c.can_no as canNo,,c.id, c.updated_status as updateStatus,c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,u.user_name as collector,c.amount,c.quantity,c.collection_date,r.route as route,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id  join route r on r.id=c.route_fk join pick_up_locations p on p.id =r.location_id  where c.collector_id =:collectorId order by c.collection_date", nativeQuery = true)
    List<CollectionsData> getBuyingCollectionsbyCollector(Long collectorId);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.updated_status as updateStatus,c.original_quantity as originalQuantity,c.session,c.id , c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,r.route as route,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk join pick_up_locations p on p.id =r.location_id   where DATE(c.collection_date) BETWEEN :fromDate and :toDate order by c.collection_date", nativeQuery = true)
    List<CollectionsData> getCollectionByDateRange(String fromDate, String toDate);

    @Query(value = "SELECT SUM(c.amount) as amount,SUM(c.quantity) as quantity,u.user_name as collector from collections c join users u on u.id =c.collector_id WHERE DATE(c.collection_date) =:date GROUP BY c.collector_id desc", nativeQuery = true)
    List<AnalyticsData> getCOllectionsPerCollectors(String date);


//    @Query(value = "select  max(id) from collections",nativeQuery = true)
//    Long getMaxVaue();

    @Query(value = "SELECT sum(c.amount)  as amount,SUM(c.quantity) as quantity from collections c where MONTH(c.collection_date)=:month and YEAR(c.collection_date)=:year  and c.collector_id=:collectorId group by c.session", nativeQuery = true)
    Optional<AnalyticsData> getCollectorRecord(Integer year, Integer month, Long collectorId);

    @Query(value = "SELECT DISTINCT  sum(c.amount) as amount,SUM(c.quantity) as quantity,c.session  from collections c where MONTH(c.collection_date)=:month and YEAR(c.collection_date)=:year  and c.collector_id=:collectorId GROUP  by c.session ", nativeQuery = true)
    List<AnalyticsData> getCollectorDataPerSerssion(Integer year, Integer month, Long collectorId);


    @Query(value = "SELECT sum(c.amount) as amount,SUM(c.quantity) as quantity ,u.user_name as collector  from collections c join users u on u.id=c.collector_id  WHERE DATE(c.collection_date)=:date  GROUP BY c.collector_id", nativeQuery = true)
    List<AnalyticsData> getCollectorDataPerDate(String date);

    @Query(value = "SELECT sum(c.amount) as amount,SUM(c.quantity) as quantity,MONTHNAME(c.collection_date) as month from collections c where YEAR (c.collection_date)=:year GROUP BY MONTH(c.collection_date)", nativeQuery = true)
    List<AnalyticsData> getCollectorDataPerYear(Integer year);

    @Query(value = "SELECT  DATE_FORMAT(c.collection_date,'%Y-%m-%d %T') as date,u.user_name as collector,f.username as farmer,c.amount,c.quantity,r.route as route, c.collection_number as collectionCode,c.current_price as currentPrice ,c.event,p.name as pickUpLocation from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk join pick_up_locations p on p.id =r.location_id   where c.collection_number =:collection_code", nativeQuery = true)
    Optional<CollectionsData> getCollectionsbyCollectionCode(String collection_code);

    @Query(value = "SELECT f.id,f.username as farmerName ,f.farmer_no as farmerNo,r.route as route,p.name as pickUpLocation from farmer f  join route r  on r.id=f.route_fk join pick_up_locations p on p.id=r.location_id  where f.farmer_no = :farmerNo", nativeQuery = true)
    Optional<FarmerDetails> getFarmerStatementDetails(Integer farmerNo);

    @Query(value = "SELECT f.username ,c.quantity,c.current_price ,c.amount ,DATE_FORMAT(c.collection_date,'%Y-%m-%d %T') as date ,c.collection_number as deliveryNumber,u.user_name,r.route as route,p.name as pickUpLocation from collections c join farmer f on c.farmer_no=f.farmer_no join users u on u.id =c.collector_id join route r on r.id=c.route_fk join pick_up_locations p on p.id=r.location_id  where c.farmer_no =:farmerNo group by c.collection_number order by c.collection_date", nativeQuery = true)
    List<FarmerCollections> getFarmerCollections(Integer farmerNo);


    @Query(value = "SELECT c.collection_number as deliveryNo,c.quantity ,c.collection_date ,c.session  from collections c join farmer f where DATE(c.collection_date) BETWEEN :from and :to and f.farmer_no =:farmerNo and c.payment_status='N'", nativeQuery = true)
    List<FarmerStmtDetails> getFarmerStmntdetails(String from, String to, Integer farmerNo);

    @Query(value = "SELECT SUM(c.quantity) as totaldeliveries,SUM(c.amount) as totalIncome ,f.farmer_no,f.username  from collections c join farmer f where DATE(c.collection_date) BETWEEN :from and :to and f.farmer_no =:farmerNo and c.payment_status=:payment_status", nativeQuery = true)
    StatementSummry getFarmerStmntSummary(String from, String to, Integer farmerNo,Character payment_status);


    @Query(nativeQuery = true, value = "select count(*) as totalCollections, :collectionDate as collectionDate from collections c where c.collector_id = :collectorId and DATE(c.collection_date)= :collectionDate")
    CurrentTotalCollections findCurrentTotalCollections(Integer collectorId, String collectionDate);

    interface StatementSummry{
        Double getTotaldeliveries();
        Double getTotalIncome();
        Integer getFarmer_no();
        String getUsername();
    }

    @Query(value = "SELECT ROUND(SUM(c.quantity),2) as quantity,ROUND(SUM(c.amount),2) as amount,p.name as location  from collections c join route r on r.id = c.route_fk join  pick_up_locations p on p.id =r.location_id GROUP BY p.name", nativeQuery = true)
    List<AnalyticsData> getQuantityPerLocation();

    @Query(value = "SELECT ROUND(SUM(c.quantity),2) as quantity,ROUND(SUM(c.amount),2) as amount,u.user_name as collector  from collections c join users u on u.id=c.collector_id  where MONTH(c.collection_date)=:month and YEAR(c.collection_date)=:year  GROUP BY c.collector_id ", nativeQuery = true)
    List<AnalyticsData> getCollectorCollections(Integer year, Integer month);

    @Query(value = "SELECT c.latitude  ,c.longitude,c.quantity,r.route as route,CAST(c.collection_date as time) as time from collections c join route r on r.id=c.route_fk WHERE c.collector_id =:collectorId   and  DATE(c.collection_date)=:date", nativeQuery = true)
    List<RouteData> getCollectorRoutes(Long collectorId, String date);

    @Query(value = "SELECT u.id,u.user_name as username,r.name as role  from users u join user_role ur on u.id=ur.user join roles r on r.id=ur.role where r.id=:roleId", nativeQuery = true)
    List<Roleusers> getRoleUsers(Long roleId);

    @Query(value = "SELECT ROUND(sum(c.amount),2) as amount , ROUND(SUM(c.quantity),2) as quantity ,MONTHNAME(c.collection_date) as month  from collections c where YEAR(c.collection_date)=:year  and c.collector_id=:collectorId GROUP BY MONTH(c.collection_date)", nativeQuery = true)
    List<AnalyticsData> getQuantityPerMonth(Integer year, Long collectorId);

    @Query(value = "SELECT ROUND(SUM(c.quantity),2) as quantity,ROUND(SUM(c.amount),2) as amount,u.user_name as collector,count(*) as ColectionsCount  from collections c join users u on u.id=c.collector_id where DATE(c.collection_date)=:date   GROUP BY c.collector_id", nativeQuery = true)
    List<AnalyticsData> getCollectorsPerCollector(String date);

    @Query(value = "SELECT ROUND(SUM(c.quantity),2) as quantity,ROUND(SUM(c.amount),2) as amount,r.route as route from collections c join route r on r.id=c.route_fk  join pick_up_locations p on p.id=r.location_id  where DATE(c.collection_date)=:date  GROUP BY r.id", nativeQuery = true)
    List<AnalyticsData> getCollectorsPerLocation(String date);
    @Query(value = "SELECT ROUND(SUM(c.quantity),2) as quantity,ROUND(SUM(c.amount),2) as amount,r.route as route,p.name as location from collections c join route r on r.id=c.route_fk join pick_up_locations p on p.id=r.location_id  where DATE(c.collection_date)=:date GROUP BY p.id", nativeQuery = true)
    List<AnalyticsData> getCollectorsPerMCCandDate(String date);
    @Query(value = "SELECT ROUND(SUM(c.quantity),2) as quantity,ROUND(SUM(c.amount),2) as amount,r.route as route,p.name as location from collections c join route r on r.id=c.route_fk join pick_up_locations p on p.id=r.location_id  where MONTHNAME(c.collection_date)=:month  GROUP BY p.id", nativeQuery = true)
    List<AnalyticsData> getCollectorsPerMCCandmonth(String month);

    @Query(value = "SELECT c.amount ,c.quantity ,c.current_price ,DATE_FORMAT(c.collection_date,'%Y-%m-%d %T') as date,c.session,c.collection_number ,u.user_name as collector,r.route as route,p.name as pickUpLocation from collections c join users u on u.id=c.collector_id join route r on r.id=c.route_fk join pick_up_locations p on p.id=r.location_id where DATE(c.collection_date) =:date", nativeQuery = true)
    List<ReportData> getCollectorsPerDate(String date);

    @Query(value = "SELECT COUNT(*) as colectionsCount,MONTHNAME(c.collection_date) as month  from collections c WHERE YEAR(c.collection_date)=:year and c.collector_id=:collectorId group by MONTH(c.collection_date)", nativeQuery = true)
    List<AnalyticsData> getCollectionCountPerMonth(Integer year, Long collectorId);



    interface Roleusers {
        Long getId();

        String getUsername();

        String getRole();
    }

    boolean existsByFarmerNoAndQuantityAndSessionAndCollectorId(Integer farmerdId, Double quantity, String session, Long collectorId);


    @Query(value = "SELECT ROUND(sum(c.amount),2)  as amount from collections c join farmer f  on f.id =c.farmer_no  where c.payment_status =:payment_status  and c.farmer_no=:farmerNo", nativeQuery = true)
    BigDecimal getPaymentAmount(Character payment_status, Integer farmerNo);

    @Query(value = "SELECT ROUND(SUM(c.quantity),2) as quantity,ROUND(SUM(c.amount),2) as amount ,c.current_price as price from collections c join farmer f  on f.id =c.farmer_no  where c.payment_status =:payment_status and c.farmer_no=:farmerNo GROUP BY c.current_price", nativeQuery = true)
    List<AnalyticsData> getCollectionsRecordsPrice(Character payment_status, Integer farmerNo);




    @Query(nativeQuery = true,value = "SELECT f.farmer_no,f.payment_mode,f.mobile_no, f.payment_freequency,f.username, p.name as CollectionCenter,r.route as route,\n" +
            "COALESCE(SUM(c.amount), 0.0) AS collectionAmount,  \n" +
            "COALESCE((SELECT SUM(fa.amount) FROM farmer_product_allocations fa\n" +
            "WHERE fa.farmer_no = f.farmer_no AND fa.payment_status ='N' and fa.status='Y' \n" +
            "and MONTHNAME(fa.allocatio_date)=:month  ), 0.0) AS allocationAmount,  \n" +
            "((COALESCE(SUM(c.amount), 0.0))-(COALESCE((SELECT SUM(fa.amount) \n" +
            "FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status ='N' \n" +
            "and MONTHNAME(fa.allocatio_date)=:month ), 0.0))) AS NetPay   FROM farmer f \n" +
            "LEFT JOIN collections c ON f.farmer_no = c.farmer_no AND c.payment_status ='N'JOIN route r on f.route_fk =r.id join pick_up_locations p on p.id=r.location_id \n" +
            "AND MONTHNAME(c.collection_date)  = :month  WHERE f.farmer_no IS NOT NULL AND f.payment_mode=:mode AND p.id =:locationId\n" +
            "GROUP BY f.farmer_no, f.username HAVING NetPay > 0")
    List<PaymentFileData> getPaymentFileData(Long locationId,String month, String mode);

    @Query(nativeQuery = true,value = "SELECT f.farmer_no,f.payment_mode,f.mobile_no, f.payment_freequency,f.username, p.name as CollectionCenter,r.route as route,bd.branch,bd.account_number,bd.account_name, \n" +
            "COALESCE(SUM(c.amount), 0.0) AS collectionAmount,  \n" +
            "COALESCE((SELECT SUM(fa.amount) FROM farmer_product_allocations fa\n" +
            "WHERE fa.farmer_no = f.farmer_no AND fa.payment_status ='N' and fa.status='Y' \n" +
            "and MONTHNAME(fa.allocatio_date)=:month  ), 0.0) AS allocationAmount,  \n" +
            "((COALESCE(SUM(c.amount), 0.0))-(COALESCE((SELECT SUM(fa.amount) \n" +
            "FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status ='N' \n" +
            "and MONTHNAME(fa.allocatio_date)=:month ), 0.0))) AS NetPay   FROM farmer f \n" +
            "LEFT JOIN collections c ON f.farmer_no = c.farmer_no AND c.payment_status ='N'JOIN route r on f.route_fk =r.id join pick_up_locations p on p.id=r.location_id join bank_details bd on bd.id =f.bank_details_id\n" +
            "AND MONTHNAME(c.collection_date)  = :month  WHERE f.farmer_no IS NOT NULL AND f.payment_mode=:mode AND p.id =:locationId\n" +
            "GROUP BY f.farmer_no, f.username HAVING NetPay > 0")
    List<PaymentFileData> getPaymentFileDataB(Long locationId,String month, String mode);
//    for mpesa
    @Query(nativeQuery = true,value = "\tSELECT CONVERT(f.farmer_no, CHAR) AS farmer_no, f.mobile_no, f.username,f.payment_mode,\n" +
            "    COALESCE(ROUND(SUM(c.amount),2), 0.0) AS collectionAmount,\n" +
            "    COALESCE(ROUND(SUM(c.quantity),2), 0.0) AS quantity,\n" +
            "    COALESCE((SELECT COALESCE(ROUND(SUM(fa.amount),2),0.0) FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status = 'N' AND fa.status = 'Y' AND DATE(fa.allocatio_date) BETWEEN :from AND :to), 0.0) AS allocationAmount,\n" +
            "    ((COALESCE(ROUND(SUM(c.amount),2), 0.0)) - (COALESCE((SELECT ROUND(SUM(fa.amount),2) FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status = 'N' AND fa.status = 'Y' AND DATE(fa.allocatio_date)  BETWEEN :from AND :to), 0.0))) AS NetPay\n" +
            "\tFROM farmer f\n" +
            "\tLEFT JOIN collections c ON f.farmer_no = c.farmer_no AND c.payment_status = 'N'\n" +
            "\tJOIN route r ON f.route_fk = r.id\n" +
            "\tJOIN pick_up_locations p ON p.id = r.location_id \n" +
            "\tWHERE f.farmer_no IS NOT NULL and f.payment_mode=:mode  AND DATE(c.collection_date) BETWEEN :from AND :to\n" +
            "\tGROUP BY f.farmer_no, f.username\n" +
            "\tHAVING NetPay > 0")
            List<PaymentFileData> getPaymentFileDataMpesaDateRange(String from,String to, String mode);

//    for bank
    @Query(nativeQuery = true,value = "SELECT CONVERT(f.farmer_no, CHAR) AS farmer_no, f.mobile_no, f.username,bd.branch,bd.account_number,bd.account_name,f.payment_mode,\n" +
            "    COALESCE(ROUND(SUM(c.amount),2), 0.0) AS collectionAmount,\n" +
            "    COALESCE(ROUND(SUM(c.quantity),2), 0.0) AS quantity,\n" +
            "    COALESCE((SELECT COALESCE(ROUND(SUM(fa.amount),2),0.0) FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status = 'N' AND fa.status = 'Y' AND DATE(fa.allocatio_date)  BETWEEN :from AND :to), 0.0) AS allocationAmount,\n" +
            "    ((COALESCE(ROUND(SUM(c.amount),2), 0.0)) - (COALESCE((SELECT ROUND(SUM(fa.amount),2) FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status = 'N' AND fa.status = 'Y' AND DATE(fa.allocatio_date)  BETWEEN :from AND :to), 0.0))) AS NetPay\n" +
            "\tFROM farmer f\n" +
            "\tLEFT JOIN collections c ON f.farmer_no = c.farmer_no AND c.payment_status = 'N'\n" +
            "\tJOIN route r ON f.route_fk = r.id\n" +
            "\tJOIN pick_up_locations p ON p.id = r.location_id join bank_details bd on bd.id =f.bank_details_id \n" +
            "\tWHERE f.farmer_no IS NOT NULL and f.payment_mode=:mode  AND DATE(c.collection_date) BETWEEN :from AND :to\n" +
            "\tGROUP BY f.farmer_no, f.username\n" +
            "\tHAVING NetPay > 0")
    List<PaymentFileData> getPaymentFileDataModeDateRange(String from,String to, String mode);

//    @Query(value = "SELECT f.farmer_no,f.payment_mode, f.payment_freequency,f.username, \n" +
//            "\tCOALESCE(SUM(c.amount), 0.0) AS collectionAmount, \n" +
//            "    COALESCE((SELECT SUM(fa.amount) FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status ='N' and fa.status='Y' and MONTHNAME(fa.allocatio_date)=:month  ), 0.0) AS allocationAmount,\n" +
//            "   ((COALESCE(SUM(c.amount), 0.0))-(COALESCE((SELECT SUM(fa.amount) FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status ='N' and MONTHNAME(fa.allocatio_date)=:month ), 0.0))) AS NetPay\n" +
//            "    FROM farmer f \n" +
//            "\tLEFT JOIN collections c ON f.farmer_no = c.farmer_no AND c.payment_status ='N' AND MONTHNAME(c.collection_date)  = :month  \n" +
//            "\tWHERE f.farmer_no IS NOT NULL AND f.payment_mode=:mode\n" +
//            "\tGROUP BY f.farmer_no, f.username HAVING NetPay > 0",nativeQuery = true)
//    List<PaymentFileData> getweeklyPaymentFileData(String week, String mode);

    @Query(value = "SELECT CONVERT(f.farmer_no,char) as farmer_no,f.payment_mode as payment_mode,f.payment_freequency as freequency , f.username as username,\n" +
            "    COALESCE(ROUND(SUM(c.amount),2), 0.0) AS collectionAmount, \n" +
            "    COALESCE((SELECT ROUND(SUM(fa.amount),2) FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status ='N' and fa.status='Y'  ), 0.0) AS allocationAmount,\n" +
            "    ((COALESCE(ROUND(SUM(c.amount),2), 0.0))-(COALESCE((SELECT ROUND(SUM(fa.amount),2) FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status ='N'  ), 0.0))) AS NetPay\n" +
            "FROM farmer f \n" +
            "    LEFT JOIN collections c ON f.farmer_no = c.farmer_no AND c.payment_status ='N' \n" +
            "WHERE f.farmer_no IS NOT NULL\n" +
            "GROUP BY f.farmer_no, f.username\n" +
            "HAVING NetPay > 0;  ",nativeQuery = true)
    List<PaymentFileData> getFarmersPaymentRecords();
    @Query(value = "SELECT CONVERT(f.farmer_no,char) as farmer_no,f.payment_mode as payment_mode,f.payment_freequency as freequency , f.username as username, \n" +
            "\tROUND(SUM(c.amount),2) AS collectionAmount, \n" +
            "    ROUND((SELECT SUM(fa.amount) FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status =:paymentStatus and fa.status='Y' and MONTHNAME(fa.allocatio_date)=:month  ),2) AS allocationAmount,\n" +
            "   ((ROUND(SUM(c.amount), 2))-(ROUND((SELECT SUM(fa.amount) FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status =:paymentStatus and MONTHNAME(fa.allocatio_date)=:month  ), 2))) AS NetPay\n" +
            "    FROM farmer f \n" +
            "\tLEFT JOIN collections c ON f.farmer_no = c.farmer_no AND c.payment_status =:paymentStatus AND MONTHNAME(c.collection_date)  = :month \n" +
            "\tWHERE f.farmer_no IS NOT NULL AND f.payment_mode = :mode\n" +
            "\tGROUP BY f.farmer_no, f.username HAVING NetPay > 0",nativeQuery = true)
    List<PaymentFileData> getFilteredFarmersPaymentRecords(String month,String mode,Character paymentStatus);
    @Query(value = "SELECT f.farmer_no, f.username,ROUND(SUM(c.amount), 2) AS collectionAmount \n" +
            "FROM collections c \n" +
            "JOIN farmer f ON f.farmer_no = c.farmer_no\n" +
            "WHERE f.farmer_no = :farmer_no AND c.payment_status ='N'",nativeQuery = true)
    Totals getTotalUnPaidAmount(Integer farmer_no);

    @Query(value = "SELECT f.farmer_no, f.username,ROUND(SUM(c.quantity),2) AS deliveries,ROUND(SUM(c.amount),2)  AS collectionAmount, \n" +
            "    (SELECT SUM(fa.amount) FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status ='N'and fa.status='Y' AND DATE(fa.allocatio_date)  BETWEEN :from AND :to) AS allocationAmount\n" +
            "FROM collections c \n" +
            "JOIN farmer f ON f.farmer_no = c.farmer_no\n" +
            "WHERE f.farmer_no = :farmer_no AND c.payment_status ='N' AND DATE(c.collection_date) BETWEEN :from and :to",nativeQuery = true)
    Totals getUnPaidAmount(Integer farmer_no,String from,String to);
    @Query(value = "SELECT f.farmer_no, f.username, ROUND(SUM(c.quantity),2) AS deliveries,ROUND(SUM(c.amount),2) AS collectionAmount, \n" +
            "    (SELECT SUM(fa.amount) FROM farmer_product_allocations fa WHERE fa.farmer_no = f.farmer_no AND fa.payment_status ='Y') AS allocationAmount\n" +
            "   FROM collections c \n" +
            "JOIN farmer f ON f.farmer_no = c.farmer_no\n" +
            "WHERE f.farmer_no = :farmer_no AND c.payment_status ='Y' AND DATE(c.collection_date) BETWEEN :from and :to",nativeQuery = true)
    Totals getPaidAmount(Integer farmer_no,String from,String to);


    interface Totals{
        Integer getFarmer_no();
        String getUsername();
        Double getCollectionAmount();
        Double getAllocationAmount();
        Double getDeliveries();
    }





}   
