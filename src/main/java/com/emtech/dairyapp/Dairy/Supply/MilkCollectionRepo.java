package com.emtech.dairyapp.Dairy.Supply;

import com.emtech.dairyapp.Analytics.AnalyticsData;
import com.emtech.dairyapp.Dairy.Interface.*;
import com.emtech.dairyapp.Reports.FarmerDetails;
import com.emtech.dairyapp.Reports.FarmerStmtDetails;
import com.emtech.dairyapp.Reports.ReportData;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MilkCollectionRepo extends JpaRepository<MilkCollections, Long> {


    List<MilkCollections> findByFarmerNo(Integer farmer_noId);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk  where c.collector_id =:collectorId and c.event= 'Collection' and DATE(c.collection_date)= :date order by c.collection_date", nativeQuery = true)
    List<CollectionsData> fetchByCollectorandDate(Long collectorId, String date);

    @Query(value = "SELECT c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,c.phone_no,u.user_name as collector,c.amount,c.quantity,c.collection_date from collections c join users u on c.collector_id =u.id  where c.collector_id =:collectorId and c.event= 'Buying' and DATE(c.collection_date) BETWEEN :from AND :to order by c.collection_date", nativeQuery = true)
    List<PurchaseData> getCollectorsPurchasesByDateRange(Long collectorId, String from,String to);
    @Query(value = "SELECT c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,c.phone_no,u.user_name as collector,c.amount,c.quantity,c.collection_date from collections c join users u on c.collector_id =u.id  where c.collector_id =:collectorId and c.event= 'Buying' and DATE(c.collection_date)= :date order by c.collection_date", nativeQuery = true)
    List<PurchaseData> getCollectorsPurchasesByDate(Long collectorId, String date);


    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk   where c.collector_id =:collectorId and c.event= 'Collection'  and DATE(collection_date) BETWEEN :from and :to ", nativeQuery = true)
    List<CollectionsData> getCollectionsByDate(Long collectorId, String from, String to);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk  where c.collector_id =:collectorId and c.payment_status =:paymentStatus and c.event= 'Buying' and DATE(collection_date) BETWEEN :from and :to ", nativeQuery = true)
    List<CollectionsData> fetchCollectorsCollectionsHistoryByDateRangeAndPaymentStatus(Long collectorId, String from, String to, Character paymentStatus);


    @Query(value = "select * from collections c where c.collector_id = :collectorId order by c.collection_date", nativeQuery = true)
    List<CollectionsData> findByCollectorId(Long collectorId);


    @Query(value = "select sum(c.quantity) as quantity,f.float_amount,f.float_spent,f.balance,u.user_name from collections c join float_manager f on c.collector_id= f.collector_id join users u on u.id=c.collector_id group by c.collector_id", nativeQuery = true)
    List<CollectionTracker> getCollectionTracker();

    @Query(value = "SELECT sum(c.quantity) as quantity,u.user_name as username,sum(c.amount) as amount FROM collections c join users u on u.id=c.collector_id WHERE DATE(c.collection_date) = CURDATE() group by c.collector_id order by c.collection_date ", nativeQuery = true)
    List<DailyRecords> getTodaysCollectionsPerCollector();

    @Query(value = "SELECT count(*) as count,sum(c.quantity) as quantity,sum(c.amount) as amount FROM collections c WHERE DATE(c.collection_date) = CURDATE()", nativeQuery = true)
    List<DailyRecords> getTodaysCollections();

    @Query(value = "SELECT count(*) as count,sum(c.quantity)  as quantity,sum(c.amount) as amount FROM collections c WHERE DATE(c.collection_date) = :date", nativeQuery = true)
    List<DailyRecords> getSpecificDateRecord(String date);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no as farmerNO, c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk  where DATE(c.collection_date)= :date order by c.collection_date", nativeQuery = true)
    List<CollectionsData> getCollectionsbyDate(String date);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no , c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk ", nativeQuery = true)
    List<CollectionsData> getAllCollections();

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no, c.id, c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer,c.amount,c.quantity,c.collection_date,r.route as route  from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk  where f.id =:farmerId order by c.collection_date", nativeQuery = true)
    List<CollectionsData> getCollectionsbyFarmer(Long farmerId);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk   where c.collector_id =:collectorId order by c.collection_date", nativeQuery = true)
    List<CollectionsData> getCollectionsbyCollector(Long collectorId);

    @Query(value = "SELECT c.id, c.collection_number as collectionCode ,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,u.user_name as collector,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id  join route r on r.id=c.route_fk   where c.collector_id =:collectorId order by c.collection_date", nativeQuery = true)
    List<CollectionsData> getBuyingCollectionsbyCollector(Long collectorId);

    @Query(value = "SELECT f.first_name ,f.last_name ,f.farmer_no,c.id , c.collection_number as collectionCode,c.event,c.current_price as currentPrice,c.product_type as productType,c.payment_status as paymentStatus,f.id as farmerId,u.user_name as collector,f.username as farmer ,c.amount,c.quantity,c.collection_date,r.route as route from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk   where DATE(c.collection_date) BETWEEN :fromDate and :toDate order by c.collection_date", nativeQuery = true)
    List<CollectionsData> getCollectionByDateRange(String fromDate, String toDate);

    @Query(value = "SELECT SUM(c.amount) as amount,CAST(SUM(c.quantity)as DECIMAL(5,2)) as quantity,u.user_name as collector from collections c join users u on u.id =c.collector_id WHERE DATE(c.collection_date) =:date GROUP BY c.collector_id desc", nativeQuery = true)
    List<AnalyticsData> getCOllectionsPerCollectors(String date);


//    @Query(value = "select  max(id) from collections",nativeQuery = true)
//    Long getMaxVaue();

    @Query(value = "SELECT sum(c.amount)  as amount,CAST(SUM(c.quantity) as DECIMAL(5,2)) as quantity from collections c where MONTH(c.collection_date)=:month and YEAR(c.collection_date)=:year  and c.collector_id=:collectorId group by c.session", nativeQuery = true)
    Optional<AnalyticsData> getCollectorRecord(Integer year, Integer month, Long collectorId);

    @Query(value = "SELECT DISTINCT  sum(c.amount) as amount,SUM(c.quantity) as quantity,c.session  from collections c where MONTH(c.collection_date)=:month and YEAR(c.collection_date)=:year  and c.collector_id=:collectorId GROUP  by c.session ", nativeQuery = true)
    List<AnalyticsData> getCollectorDataPerSerssion(Integer year, Integer month, Long collectorId);


    @Query(value = "SELECT sum(c.amount) as amount,CAST(SUM(c.quantity)as DECIMAL(5,2)) as quantity ,u.user_name as collector  from collections c join users u on u.id=c.collector_id  WHERE DATE(c.collection_date)=:date  GROUP BY c.collector_id", nativeQuery = true)
    List<AnalyticsData> getCollectorDataPerDate(String date);

    @Query(value = "SELECT sum(c.amount) as amount,CAST(SUM(c.quantity)as DECIMAL(5,2)) as quantity,MONTHNAME(c.collection_date) as month from collections c where YEAR (c.collection_date)=:year GROUP BY MONTH(c.collection_date)", nativeQuery = true)
    List<AnalyticsData> getCollectorDataPerYear(Integer year);

    @Query(value = "SELECT  DATE_FORMAT(c.collection_date,'%Y-%m-%d %T') as date,u.user_name as collector,f.username as farmer,c.amount,c.quantity,r.route as route, c.collection_number as collectionCode,c.current_price as currentPrice ,c.event from collections c join users u on c.collector_id =u.id join farmer f on f.farmer_no=c.farmer_no join route r on r.id=c.route_fk  where c.collection_number =:collection_code", nativeQuery = true)
    Optional<CollectionsData> getCollectionsbyCollectionCode(String collection_code);

    @Query(value = "SELECT f.id,f.username as farmerName ,f.farmer_no as farmerNo,r.route as route,p.name as pickUpLocation from farmer f  join route r  on r.id=f.route_fk join pick_up_locations p on p.id=r.location_id  where f.farmer_no = :farmerNo", nativeQuery = true)
    Optional<FarmerDetails> getFarmerStatementDetails(Integer farmerNo);

    @Query(value = "SELECT f.username ,c.quantity,c.current_price ,c.amount ,DATE_FORMAT(c.collection_date,'%Y-%m-%d %T') as date ,c.collection_number as deliveryNumber,u.user_name,r.route as route,p.name as pickUpLocation from collections c join farmer f on c.farmer_no=f.farmer_no join users u on u.id =c.collector_id join route r on r.id=c.route_fk join pick_up_locations p on p.id=r.location_id  where c.farmer_no =:farmerNo group by c.collection_number order by c.collection_date", nativeQuery = true)
    List<FarmerCollections> getFarmerCollections(Integer farmerNo);


    @Query(value = "SELECT c.collection_number as deliveryNo,c.quantity ,c.collection_date ,c.session  from collections c join farmer f where DATE(c.collection_date) BETWEEN :from and :to and f.farmer_no =:farmerNo", nativeQuery = true)
    List<FarmerStmtDetails> getFarmerStmntdetails(String from, String to, Integer farmerNo);

    @Query(value = "SELECT SUM(c.quantity) as totaldeliveries,SUM(c.amount) as totalIncome ,f.farmer_no,f.username  from collections c join farmer f where DATE(c.collection_date) BETWEEN :from and :to and f.farmer_no =:farmerNo and c.payment_status=:payment_status", nativeQuery = true)
    StatementSummry getFarmerStmntSummary(String from, String to, Integer farmerNo,Character payment_status);

    interface StatementSummry{
        Double getTotaldeliveries();
        Double getTotalIncome();
        Integer getFarmer_no();
        String getUsername();
    }

    @Query(value = "SELECT CAST(SUM(c.quantity)as DECIMAL(5,2)) as quantity,p.name as location  from collections c join pick_up_locations p on p.id =c.pick_up_location GROUP BY p.name", nativeQuery = true)
    List<AnalyticsData> getQuantityPerLocation();

    @Query(value = "SELECT sum(c.amount) as amount ,CAST(SUM(c.quantity)as DECIMAL(5,2)) as quantity,u.user_name as collector  from collections c join users u on u.id=c.collector_id  where MONTH(c.collection_date)=:month and YEAR(c.collection_date)=:year  GROUP BY c.collector_id ", nativeQuery = true)
    List<AnalyticsData> getCollectorCollections(Integer year, Integer month);

    @Query(value = "SELECT c.latitude  ,c.longitude,c.quantity,r.route as route,CAST(c.collection_date as time) as time from collections c join route r on r.id=c.route_fk WHERE c.collector_id =:collectorId   and  DATE(c.collection_date)=:date", nativeQuery = true)
    List<RouteData> getCollectorRoutes(Long collectorId, String date);

    @Query(value = "SELECT u.id,u.user_name as username,r.name as role  from users u join user_role ur on u.id=ur.user join roles r on r.id=ur.role where r.id=:roleId", nativeQuery = true)
    List<Roleusers> getRoleUsers(Long roleId);

    @Query(value = "SELECT sum(c.amount) as amount ,CAST(SUM(c.quantity) as DECIMAL(5,2)) as quantity ,MONTHNAME(c.collection_date) as month  from collections c where YEAR(c.collection_date)=:year  and c.collector_id=:collectorId GROUP BY MONTH(c.collection_date)", nativeQuery = true)
    List<AnalyticsData> getQuantityPerMonth(Integer year, Long collectorId);

    @Query(value = "SELECT sum(c.amount) as amount ,CAST(SUM(c.quantity)as DECIMAL(5,2)) as quantity,u.user_name as collector,count(*) as ColectionsCount  from collections c join users u on u.id=c.collector_id where DATE(c.collection_date)=:date   GROUP BY c.collector_id", nativeQuery = true)
    List<AnalyticsData> getCollectorsPerCollector(String date);

    @Query(value = "SELECT sum(c.amount) as amount ,CAST(SUM(c.quantity)as DECIMAL(5,2)) as quantity,r.route as route from collections c join route r on r.id=c.route_fk  where DATE(c.collection_date)=:date GROUP BY r.id;", nativeQuery = true)
    List<AnalyticsData> getCollectorsPerLocation(String date);

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


    @Query(value = "SELECT sum(c.amount)  as amount from collections c join farmer f  on f.id =c.farmer_no  where c.payment_status =:payment_status  and c.farmer_no=:farmerNo", nativeQuery = true)
    BigDecimal getPaymentAmount(Character payment_status, Integer farmerNo);

    @Query(value = "SELECT sum(c.amount)  as amount,sum(c.quantity) as quantity ,c.current_price as price from collections c join farmer f  on f.id =c.farmer_no  where c.payment_status =:payment_status and c.farmer_no=:farmerNo GROUP BY c.current_price", nativeQuery = true)
    List<AnalyticsData> getCollectionsRecordsPrice(Character payment_status, Integer farmerNo);



    @Query(value = "SELECT f.farmer_no,f.payment_mode, f.username, \n" +
            "\tCOALESCE(SUM(c.amount), 0.0) AS collectionAmount, \n" +
            "    COALESCE((SELECT SUM(fa.amount) FROM farmer_product_allocations fa WHERE fa.farmer_id = f.farmer_no AND fa.payment_status ='N' and MONTHNAME(fa.allocatio_date)=:month  ), 0.0) AS allocationAmount,\n" +
            "   ((COALESCE(SUM(c.amount), 0.0))-(COALESCE((SELECT SUM(fa.amount) FROM farmer_product_allocations fa WHERE fa.farmer_id = f.farmer_no AND fa.payment_status ='N' and MONTHNAME(fa.allocatio_date)=:month ), 0.0))) AS NetPay\n" +
            "    FROM farmer f \n" +
            "\tLEFT JOIN collections c ON f.farmer_no = c.farmer_no AND c.payment_status ='N' AND MONTHNAME(c.collection_date)  = :month  \n" +
            "\tWHERE f.farmer_no IS NOT NULL AND f.payment_mode=:mode\n" +
            "\tGROUP BY f.farmer_no, f.username",nativeQuery = true)
    List<PaymentFileDate> getPaymentFileData(String month,String mode);

    interface  PaymentFileDate{
        String getFarmer_no();
        String getPayment_mode();
        String getUsername();
        Double getCollectionAmount();
        Double getAllocationAmount();
        Double getNetPay();

    }



}
