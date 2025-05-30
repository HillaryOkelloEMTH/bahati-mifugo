package com.emtech.dairyapp.Configurations.FarmerManagement;

import com.emtech.dairyapp.Configurations.Interfaces.FarmerAccruedAmount;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerData;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmersPerWard;
import com.emtech.dairyapp.Dairy.Interface.CurrentTotalFarmers;
import com.emtech.dairyapp.Reports.Dto.PayrollInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepo extends JpaRepository<Farmer,Long> {



    List<Farmer> findByDeletedFlag(Character deletedFlag);
    List<Farmer> findByWardFk(Long wardId);

    boolean existsByIdNumber(String idNumber);

    @Query(value = "select * from farmer where farmer_no = :farmer_no limit 1", nativeQuery = true)
    Optional<Farmer> getByFarmerNo(Integer farmer_no);

    @Query(value = "select f.username, f.farmer_no as fno, f.mobile_no, f.id_number as idno, r.route, t.username as collector,pul.name as mcc, b.bank_name as bank, \n"
            + " b.account_number as accno, b.account_name as accname, b.branch, sc.name as subcounty, c.name as county, \n"
            +" f.gender from farmer f join route r on f.route_fk=r.id join pick_up_locations pul on r.location_id=pul.id join collector t on r.location_id=t.location_id left join bank_details b on f.bank_details_id=b.id left join subcounty sc on f.subcounty_fk=sc.id left join county c on sc.county_fk=c.id where f.farmer_no= :farmerNo", nativeQuery = true)
    Optional<FarmerData> getFarmerData(Integer farmerNo);

    @Query(value = "select * from farmer where route_fk= :routeId", nativeQuery = true)
    List<Farmer> getFarmersPerRoute(Long routeId);

    @Query(value = " select case when count(*) > 0 then true else false end from farmer where id_number= :idNo", nativeQuery = true)
    Integer farmerExistsById(String idNo);

    @Query(value = " select case when count(*) > 0 then true else false end from farmer where mobile_no= :mobileNo", nativeQuery = true)
    Integer farmerExistsByMobile(String mobileNo);

    @Query(value = "select count(*) from farmer",nativeQuery = true)
    Integer getCount();
    @Query(value = "SELECT f.id,f.username,f.payment_freequency,r.route as route ,r.id as routeId,f.first_name,b.account_name ,b.account_number,f.alternative_mobile_no   ,f.id_number ,f.created_at ,f.payment_mode ,f.deleted_flag,f.mobile_no ,f.member_type ,f.no_of_cows ,f.farmer_no ,s.name as subcounty,c.name as county,p.name as pickUpLocation from farmer f join ward w  on f.ward_fk =w.id join subcounty s on s.id =f.subcounty_fk join county c on c.id =s.county_fk join route r on r.id=f.route_fk join pick_up_locations p on p.id =r.location_id join bank_details b on b.id =f.bank_details_id where f.id=:farmerId",nativeQuery = true)
    Optional<FarmerInfo> getfarmerDetails(Long farmerId);
    @Query(value = "SELECT f.id,f.username,f.payment_freequency,r.route as route,r.id as routeId ,f.first_name as name,b.account_name ,b.account_number  ,f.alternative_mobile_no  ,f.id_number ,f.created_at ,f.payment_mode ,f.deleted_flag,f.mobile_no ,f.member_type ,f.no_of_cows ,f.farmer_no ,s.name as subcounty,c.name as county,p.name as pickUpLocation from farmer f left join ward w  on f.ward_fk =w.id left join subcounty s on s.id =f.subcounty_fk left join county c on c.id =s.county_fk left join route r on r.id=f.route_fk left join pick_up_locations p on p.id =r.location_id left join bank_details b on b.id =f.bank_details_id",nativeQuery = true)
    List<FarmerInfo> getAllfarmers();

    @Query(value = "SELECT f.id,f.username,f.payment_freequency,r.route as route,r.id as routeId ,f.first_name as name,b.account_name ,b.account_number  ,f.alternative_mobile_no  ,f.id_number ,f.created_at ,f.payment_mode ,f.deleted_flag,f.mobile_no ,f.member_type ,f.no_of_cows ,f.farmer_no ,s.name as subcounty,c.name as county,p.name as pickUpLocation from farmer f join collections cl on f.farmer_no = cl.farmer_no left join ward w  on f.ward_fk =w.id left join subcounty s on s.id =f.subcounty_fk left join county c on c.id =s.county_fk left join route r on r.id=f.route_fk left join pick_up_locations p on p.id =r.location_id left join bank_details b on b.id =f.bank_details_id where cl.collection_date >= date_sub(now(), interval :months month) group by cl.farmer_no",nativeQuery = true)
    List<FarmerInfo> getActiveFarmers(int months);

    @Query(value = """
            SELECT f.id,f.username,f.payment_freequency,r.route as route,r.id as routeId ,f.first_name as name,b.account_name ,b.account_number \s
            ,f.alternative_mobile_no  ,f.id_number ,f.created_at ,f.payment_mode ,f.deleted_flag,f.mobile_no ,f.member_type ,f.no_of_cows ,f.farmer_no \s
            ,s.name as subcounty,c.name as county,p.name as pickUpLocation from farmer f join collections cl on f.farmer_no = cl.farmer_no left join ward w  on f.ward_fk =w.id left join subcounty s on s.id =f.subcounty_fk left join county c on c.id =s.county_fk left join route r on r.id=f.route_fk left join pick_up_locations p on p.id =r.location_id left join bank_details b on b.id =f.bank_details_id where cl.collection_date >= date_sub(now(), interval :months month) and f.route_fk= :routeFk group by f.farmer_no""", nativeQuery = true)
    List<FarmerInfo> getRouteActiveFarmers(int months, Long routeFk);

    @Query(value = """
            SELECT f.id,f.username,f.payment_freequency,r.route as route,r.id as routeId ,f.first_name as name,b.account_name ,b.account_number \s
            ,f.alternative_mobile_no  ,f.id_number ,f.created_at ,f.payment_mode ,f.deleted_flag,f.mobile_no ,f.member_type ,f.no_of_cows ,f.farmer_no \s
            ,s.name as subcounty,c.name as county,p.name as pickUpLocation from farmer f join collections cl on f.farmer_no = cl.farmer_no left join ward w  on f.ward_fk =w.id left join subcounty s on s.id =f.subcounty_fk left join county c on c.id =s.county_fk left join route r on r.id=f.route_fk left join pick_up_locations p on p.id =r.location_id left join bank_details b on b.id =f.bank_details_id where cl.collection_date >= date_sub(now(), interval :months month) and p.id= :locationId group by f.farmer_no""", nativeQuery = true)
    List<FarmerInfo> getCenterActiveFarmers(int months, Long locationId);

    @Query(value = "SELECT f.id,f.username,r.route as routeName,r.id as routeFk ,f.first_name as name, f.alternative_mobile_no  ,f.id_number as idNumber,f.created_at ,f.payment_mode ,f.deleted_flag,f.mobile_no as mobileNo,f.farmer_no as farmerNo,p.name as pickUpLocation from farmer f left join route r on r.id=f.route_fk left join pick_up_locations p on p.id =r.location_id where r.location_id= :locationId", nativeQuery = true)
    List<FarmerInterface> getMccfarmers(Long locationId);
    @Query(value = "\n" +
            "SELECT DISTINCT  f.*  from farmer f left join route r on r.id=f.route_fk  left join subcounty s on s.id =f.subcounty_fk \n" +
            "left join county c on c.id =s.county_fk left JOIN pick_up_locations p on p.id=r.location_id join collector c2 on c2.location_id =p.id \n" +
            "join users u on u.user_name =c2.username where u.id =:collectorId group by f.id",nativeQuery = true)
    List<Farmer> getfarmersPerCollector(Long collectorId);

    @Query(value = "SELECT DISTINCT  f.*  from farmer f left join route r on r.id=f.route_fk  left join subcounty s on s.id =f.subcounty_fk left join county c on c.id =s.county_fk left JOIN pick_up_locations p on p.id=r.location_id join transporter t on t.route_id =r.id join users u on u.user_name =t.username where u.id = :transporterId group by f.id", nativeQuery = true)
    List<Farmer> getFarmersPerTransporter(Long transporterId);

    @Query(value = "select  max(farmer_no) from farmer",nativeQuery = true)
    Integer getMaxVaue();
     boolean existsByFarmerNo(Integer memberNo);
    @Query(value = "select count(*) as farmers,w.name as ward  from farmer f join ward w on w.id =f.ward_fk  GROUP by w.name",nativeQuery = true)
    List<FarmersPerWard> getFarmersPerWard();


    @Query(value = "SELECT f.id,f.username,f.payment_freequency,r.route as route ,r.id as routeId,f.first_name as name, f.last_name, b.account_name ,b.account_number  ,f.alternative_mobile_no   ,f.id_number ,f.created_at ,f.payment_mode ,f.deleted_flag,f.mobile_no ,f.member_type ,f.no_of_cows ,f.farmer_no ,s.name as subcounty,c.name as county,p.name as pickUpLocation from farmer f left join ward w  on f.ward_fk =w.id left join subcounty s on s.id =f.subcounty_fk left join county c on c.id =s.county_fk left join route r on r.id=f.route_fk left join pick_up_locations p on p.id =r.location_id left join bank_details b on b.id =f.bank_details_id where f.farmer_no=:farmer_no",nativeQuery = true)
    Optional<FarmerInfo> findByFarmerNo(Integer farmer_no);


@Query(value = "SELECT SUM(c.amount) as amount,SUM(c.quantity) as quantity  from collections c  join farmer f  on f.farmer_no=c.farmer_no  WHERE f.id = :id and c.payment_status = :paymentyStatus",nativeQuery = true)
FarmerAccruedAmount getFarmerAccruedAmount(Long id, Character paymentyStatus);



    boolean existsById(Long id);

    @Query(nativeQuery = true, value = "select count(*) as totalFarmers from farmer f join route r on f.route_fk=r.id join collector c on r.location_id=c.location_id join users u on c.username=u.user_name where u.id= :collectorId and MONTH(f.created_at) = :month ")
    CurrentTotalFarmers fetchCurrentAndPreviousCollectionsAndFarmersCount(Integer collectorId, Integer month);

    @Query(value = "SELECT c.farmer_no AS fno, " +
            "ROUND(SUM(c.quantity), 2) AS qty, " +
            "ROUND(SUM(c.amount), 2) AS income, " +
            "concat(f.first_name,' ', ifnull(f.middle_name, ' '), ' ',f.last_name) as farmer, " +
            "f.mobile_no AS mobileNo, " +
            "r.route, " +
            "p.name AS mcc, " +
            "c.current_price as price, " +
            "(select round(coalesce(sum(case when fpa2.product_name like 'dairy%' then fpa2.amount else 0 end), 0), 2) " +
            "from farmer_product_allocations fpa2 WHERE MONTH(fpa2.approval_date) = :month " +
            "AND YEAR(fpa2.approval_date) = :year and fpa2.farmer_no=c.farmer_no) as dairyMeal, " +
            "(select round(coalesce(sum(case when fpa2.product_name not like 'dairy%' then fpa2.amount else 0 end), 0), 2) " +
            "from farmer_product_allocations fpa2 WHERE MONTH(fpa2.approval_date) = :month " +
            "AND YEAR(fpa2.approval_date) = :year and fpa2.farmer_no=c.farmer_no) as salts, " +
            "(SELECT ROUND(COALESCE(SUM(fpa.amount), 0.0), 2) " +
            " FROM farmer_product_allocations fpa " +
            " WHERE MONTH(fpa.requested_on) = :month " +
            "   AND YEAR(fpa.requested_on) = :year " +
            "   AND fpa.farmer_no = c.farmer_no) AS expenses, " +
            "(ROUND(SUM(c.amount), 2) - " +
            " (SELECT ROUND(COALESCE(SUM(fpa.amount), 0.0), 2) " +
            "  FROM farmer_product_allocations fpa " +
            "  WHERE MONTH(fpa.requested_on) = :month " +
            "    AND YEAR(fpa.requested_on) = :year " +
            "    AND fpa.farmer_no = c.farmer_no)) AS netpay, " +
            "b.bank_name AS bname, " +
            "b.account_number AS accno, " +
            "b.branch " +
            "FROM collections c " +
            "LEFT JOIN farmer f ON c.farmer_no = f.farmer_no " +
//            "LEFT JOIN farmer_product_allocations fpa ON f.farmer_no = fpa.farmer_no " + almost f*ckd -- up join
            "LEFT JOIN route r ON c.route_fk = r.id " +
            "LEFT JOIN pick_up_locations p ON r.location_id = p.id " +
            "LEFT JOIN bank_details b ON f.bank_details_id = b.id " +
            "WHERE MONTH(c.collection_date) = :month " +
            "  AND YEAR(c.collection_date) = :year " +
            "  AND c.quantity > 0 " +
            "GROUP BY c.farmer_no " +
            "ORDER BY p.id asc, c.farmer_no asc", nativeQuery = true)
    List<PayrollInterface> getFarmerPayroll(Integer month, String year);

}
