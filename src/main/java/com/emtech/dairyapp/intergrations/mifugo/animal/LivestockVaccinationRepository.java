package com.emtech.dairyapp.intergrations.mifugo.animal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivestockVaccinationRepository extends JpaRepository<LivestockVaccination, Long> {

    List<LivestockVaccination> findByAnimalTagOrderByAdministeredDateDesc(String animalTag);

    List<LivestockVaccination> findByFarmerNationalIdOrderByAdministeredDateDesc(String farmerNationalId);

    long countByStatus(String status);

    @Query("SELECT v.targetDisease, COUNT(v) FROM LivestockVaccination v GROUP BY v.targetDisease")
    List<Object[]> countByDiseaseGroup();
}

