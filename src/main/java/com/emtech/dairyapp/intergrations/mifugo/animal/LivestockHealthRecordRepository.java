package com.emtech.dairyapp.intergrations.mifugo.animal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivestockHealthRecordRepository extends JpaRepository<LivestockHealthRecord, Long> {

    List<LivestockHealthRecord> findByAnimalTagOrderByEventDateDesc(String animalTag);

    List<LivestockHealthRecord> findByRecordType(String recordType);
}

