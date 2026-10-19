package com.emtech.dairyapp.intergrations.mifugo.animal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivestockAnimalRepository extends JpaRepository<LivestockAnimal, Long> {

    Optional<LivestockAnimal> findByAnimalTag(String animalTag);

    List<LivestockAnimal> findByFarmerNationalId(String farmerNationalId);

    List<LivestockAnimal> findByFarmerNo(Integer farmerNo);

    List<LivestockAnimal> findByStatus(String status);

    @Query("SELECT a FROM LivestockAnimal a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:breed IS NULL OR LOWER(a.breed) LIKE LOWER(CONCAT('%', :breed, '%'))) AND " +
           "(:search IS NULL OR LOWER(a.animalTag) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.animalName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.farmerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "a.farmerNationalId LIKE CONCAT('%', :search, '%')) ORDER BY a.id ASC")
    List<LivestockAnimal> searchAnimals(@Param("status") String status,
                                        @Param("breed") String breed,
                                        @Param("search") String search);

    long countByStatus(String status);

    long countByLactationStatus(String lactationStatus);

    @Query("SELECT a.breed, COUNT(a) FROM LivestockAnimal a GROUP BY a.breed")
    List<Object[]> countByBreedGroup();

    @Query("SELECT a.status, COUNT(a) FROM LivestockAnimal a GROUP BY a.status")
    List<Object[]> countByStatusGroup();
}

