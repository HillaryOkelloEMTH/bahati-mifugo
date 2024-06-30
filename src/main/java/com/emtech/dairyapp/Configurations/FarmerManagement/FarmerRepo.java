package com.emtech.dairyapp.Configurations.FarmerManagement;

import com.emtech.dairyapp.Configurations.Interfaces.FarmerAccruedAmount;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmersPerWard;
import com.emtech.dairyapp.Dairy.Interface.CurrentTotalFarmers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepo extends JpaRepository<Farmer,Long> {



    List<Farmer> findByDeletedFlag(Character deletedFlag);
    List<Farmer> findByWardFk(Long wardId);
    Optional<Farmer> findById(Long id);

    @Query(value = "select count(*) from farmer",nativeQuery = true)
    Integer getCount();
    @Query(value = "SELECT f.id,f.username,f.payment_freequency,r.route as route ,r.id as routeId,f.first_name,b.account_name ,b.account_number  ,f.alternative_mobile_no   ,f.id_number ,f.created_at ,f.payment_mode ,f.deleted_flag,f.mobile_no ,f.member_type ,f.no_of_cows ,f.farmer_no ,s.name as subcounty,c.name as county,p.name as pickUpLocation from farmer f join ward w  on f.ward_fk =w.id join subcounty s on s.id =f.subcounty_fk join county c on c.id =s.county_fk join route r on r.id=f.route_fk join pick_up_locations p on p.id =r.location_id join bank_details b on b.id =f.bank_details_id where f.id=:farmerId",nativeQuery = true)
    Optional<FarmerInfo> getfarmerDetails(Long farmerId);
    @Query(value = "SELECT f.id,f.username,f.payment_freequency,r.route as route,r.id as routeId ,f.first_name as name,b.account_name ,b.account_number  ,f.alternative_mobile_no  ,f.id_number ,f.created_at ,f.payment_mode ,f.deleted_flag,f.mobile_no ,f.member_type ,f.no_of_cows ,f.farmer_no ,s.name as subcounty,c.name as county,p.name as pickUpLocation from farmer f left join ward w  on f.ward_fk =w.id left join subcounty s on s.id =f.subcounty_fk left join county c on c.id =s.county_fk left join route r on r.id=f.route_fk left join pick_up_locations p on p.id =r.location_id left join bank_details b on b.id =f.bank_details_id",nativeQuery = true)
    List<FarmerInfo> getAllfarmers();
    @Query(value = "\n" +
            "SELECT DISTINCT  f.*  from farmer f left join route r on r.id=f.route_fk  left join subcounty s on s.id =f.subcounty_fk \n" +
            "left join county c on c.id =s.county_fk left JOIN pick_up_locations p on p.id=r.location_id join collector c2 on c2.location_id =p.id \n" +
            "join users u on u.user_name =c2.username where u.id =:collectorId group by f.id",nativeQuery = true)
    List<Farmer> getfarmersPerCollector(Long collectorId);

    @Query(value = "select  max(id) from farmer",nativeQuery = true)
    Integer getMaxVaue();
     boolean existsByFarmerNo(Integer memberNo);
    @Query(value = "select count(*) as farmers,w.name as ward  from farmer f join ward w on w.id =f.ward_fk  GROUP by w.name",nativeQuery = true)
    List<FarmersPerWard> getFarmersPerWard();


    @Query(value = "SELECT f.id,f.username,f.payment_freequency,r.route as route ,r.id as routeId,f.first_name as name,b.account_name ,b.account_number  ,f.alternative_mobile_no   ,f.id_number ,f.created_at ,f.payment_mode ,f.deleted_flag,f.mobile_no ,f.member_type ,f.no_of_cows ,f.farmer_no ,s.name as subcounty,c.name as county,p.name as pickUpLocation from farmer f left join ward w  on f.ward_fk =w.id left join subcounty s on s.id =f.subcounty_fk left join county c on c.id =s.county_fk left join route r on r.id=f.route_fk left join pick_up_locations p on p.id =r.location_id left join bank_details b on b.id =f.bank_details_id where f.farmer_no=:farmer_no",nativeQuery = true)
    Optional<FarmerInfo> findByFarmerNo(Integer farmer_no);


@Query(value = "SELECT SUM(c.amount) as amount,SUM(c.quantity) as quantity  from collections c  join farmer f  on f.farmer_no=c.farmer_no  WHERE f.id = :id and c.payment_status = :paymentyStatus",nativeQuery = true)
FarmerAccruedAmount getFarmerAccruedAmount(Long id, Character paymentyStatus);



    boolean existsById(Long id);

    @Query(nativeQuery = true, value = "select count(*) as totalFarmers from farmer f join route r on f.route_fk=r.id join collector c on r.location_id=c.location_id join users u on c.username=u.user_name where u.id= :collectorId and MONTH(f.created_at) = :month ")
    CurrentTotalFarmers fetchCurrentAndPreviousCollectionsAndFarmersCount(Integer collectorId, Integer month);
}
