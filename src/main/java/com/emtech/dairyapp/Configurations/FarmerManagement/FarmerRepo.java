package com.emtech.dairyapp.Configurations.FarmerManagement;

import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
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
    @Query(value = "SELECT f.username ,f.first_name,f.address ,f.alternative_mobile_no ,f.bank_account_no ,f.last_name ,f.id_number ,f.created_at ,f.deleted_flag,f.mobile_no ,f.member_type ,f.no_of_cows ,f.member_code ,s.name as subcounty,c.name as county from farmer f join ward w  on f.ward_fk =w.id join subcounty s on s.id =f.subcounty_fk join county c on c.id =s.county_fk where f.id=:farmerId",nativeQuery = true)
    Optional<FarmerInfo> getfarmerDetails(Long farmerId);

    @Query(value = "SELECT DISTINCT  f.*  from farmer f join ward w  on f.ward_fk =w.id join subcounty s on s.id =f.subcounty_fk join county c on c.id =s.county_fk\n" +
            "join pick_up_locations p on p.ward_fk =w.id  join collector c2 on c2.location_id =p.id join users u on u.user_name =c2.username where u.id =:collectorId group by f.id",nativeQuery = true)
    List<Farmer> getfarmersPerCollector(Long collectorId);

    @Query(value = "select  max(id) from farmer",nativeQuery = true)
    Long getMaxVaue();

}
