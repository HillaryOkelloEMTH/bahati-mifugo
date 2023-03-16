package com.emtech.dairyapp.Dairy.Supply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MilkCollectionRepo extends JpaRepository<MilkCollections,Long> {



    List<MilkCollections> findByMember(Long memberId);

    @Query(value = "select * from collections c where c.collector_id =:collector_id and c.collection_date = :collectionDate",nativeQuery = true)
    List<MilkCollections> fetchByCollectorandDate(Long collector_id,String collectionDate);




    @Query(value = "select * from collections c join farmer f on f.id =c.member where c.collector_id =:collector_id and collection_date BETWEEN :from and :to ;",nativeQuery = true)
    List<MilkCollections> getCollectionsByDate(Long collector_id,String from,String to);



}
