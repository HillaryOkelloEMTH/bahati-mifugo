package com.emtech.dairyapp.Dairy.Supply;

import com.emtech.dairyapp.Dairy.Interface.CollectionTracker;
import com.emtech.dairyapp.Dairy.Interface.DailyRecords;
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

    List<MilkCollections> findByCollectorId(Long collectorId);

    @Query(value = "select sum(c.quantity) as quantity,f.float_amount,f.balance,u.user_name from collections c join float_manager f on c.collector_id= f.collector_id join users u on u.id=c.collector_id group by c.collector_id",nativeQuery = true)
    List<CollectionTracker> getCollectionTracker();

    @Query(value = "SELECT sum(c.quantity) as quantity,u.user_name as username,sum(c.amount) as amount FROM collections c join users u on u.id=c.collector_id WHERE DATE(c.collection_date) = CURDATE() group by c.collector_id",nativeQuery = true)
    List<DailyRecords> getTodaysCollectionsPerCollector();

    @Query(value = "SELECT sum(c.quantity) as quantity,sum(c.amount) as amount FROM collections c WHERE DATE(c.collection_date) = CURDATE();",nativeQuery = true)
    List<DailyRecords> getTodaysCollections();






}
