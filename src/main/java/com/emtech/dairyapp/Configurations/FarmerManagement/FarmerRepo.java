package com.emtech.dairyapp.Configurations.FarmerManagement;

import com.emtech.dairyapp.Configurations.Interfaces.FarmerAccruedAmount;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmersPerWard;
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
    @Query(value = "SELECT f.id,f.username,f.payment_freequency,p.name as pickUpLocation ,f.first_name,f.address ,f.alternative_mobile_no  ,f.last_name ,f.id_number ,f.created_at ,f.deleted_flag,f.mobile_no ,f.member_type ,f.no_of_cows ,f.farmer_no ,s.name as subcounty,c.name as county from farmer f join ward w  on f.ward_fk =w.id join subcounty s on s.id =f.subcounty_fk join county c on c.id =s.county_fk join pick_up_locations p on p.id=f.pickup_location where f.id=:farmerId",nativeQuery = true)
    Optional<FarmerInfo> getfarmerDetails(Long farmerId);

    @Query(value = "SELECT DISTINCT  f.*  from farmer f join ward w  on f.ward_fk =w.id join subcounty s on s.id =f.subcounty_fk join county c on c.id =s.county_fk\n" +
            "join pick_up_locations p on p.ward_fk =w.id  join collector c2 on c2.location_id =p.id join users u on u.user_name =c2.username where u.id =:collectorId group by f.id",nativeQuery = true)
    List<Farmer> getfarmersPerCollector(Long collectorId);

    @Query(value = "select  max(id) from farmer",nativeQuery = true)
    Integer getMaxVaue();
     boolean existsByFarmerNo(Integer memberNo);
//    default boolean existsByValuePlusOne(String value) {
//        return existsByMemberCode(value + 1);
//    }
    @Query(value = "select count(*) as farmers,w.name as ward  from farmer f join ward w on w.id =f.ward_fk  GROUP by w.name",nativeQuery = true)
    List<FarmersPerWard> getFarmersPerWard();


    @Query(value = "SELECT f.id,f.username,f.payment_freequency,p.id as pickupLocationId,p.name as pickUpLocation ,f.address  ,f.id_number ,f.mobile_no ,f.member_type ,f.no_of_cows ,f.farmer_no as farmeNo,s.name as subcounty,c.name as county from farmer f join ward w  on f.ward_fk =w.id join subcounty s on s.id =f.subcounty_fk join county c on c.id =s.county_fk join pick_up_locations p on p.id=f.pickup_location  where f.farmer_no=:memberCode",nativeQuery = true)
    Optional<FarmerInfo> findByFarmerNo(Integer memberCode);

@Query(value = "SELECT SUM(c.amount) as amount,SUM(c.quantity) as quantity  from collections c  join farmer f  on f.farmer_no=c.farmer_no  WHERE f.id = :id and c.payment_status = :paymentyStatus",nativeQuery = true)
FarmerAccruedAmount getFarmerAccruedAmount(Long id, Character paymentyStatus);



    boolean existsById(Long id);

}
