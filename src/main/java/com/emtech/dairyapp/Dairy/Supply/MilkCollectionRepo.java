package com.emtech.dairyapp.Dairy.Supply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MilkCollectionRepo extends JpaRepository<MilkCollections,Long> {



    List<MilkCollections> findByMember(Long memberId);
    List<MilkCollections> findByCollectionDateAndAndCollectorId(Date collection_date,Long collectorId);



//    @Query(value = "",nativeQuery = true)
//    List<MilkCollections>
//    List<MilkCollections> getCollectionsByDate(Long collectorId,Date from,Date to);



}
