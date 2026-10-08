package com.emtech.dairyapp.intergrations.mifugo.claim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivestockClaimRepository extends JpaRepository<LivestockClaim, Long> {

    Optional<LivestockClaim> findByClaimReference(String claimReference);

    List<LivestockClaim> findAllByOrderByCreatedAtDesc();

    List<LivestockClaim> findByClaimStatusOrderByCreatedAtDesc(String claimStatus);

    List<LivestockClaim> findByFarmerNationalIdOrderByCreatedAtDesc(String nationalId);

    List<LivestockClaim> findByAnimalIdOrderByCreatedAtDesc(String animalId);

    @Query("SELECT c FROM LivestockClaim c WHERE " +
            "(:status IS NULL OR c.claimStatus = :status) AND " +
            "(:nationalId IS NULL OR c.farmerNationalId = :nationalId) AND " +
            "(:search IS NULL OR " +
            "LOWER(c.claimReference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.farmerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.farmerNationalId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.animalTag) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.policyNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY c.createdAt DESC")
    List<LivestockClaim> searchClaims(
            @Param("status") String status,
            @Param("nationalId") String nationalId,
            @Param("search") String search);

    long countByClaimStatus(String claimStatus);

    long countByBiometricStatus(String biometricStatus);

    @Query("SELECT COALESCE(SUM(c.sumInsured), 0.0) FROM LivestockClaim c")
    Double sumTotalInsurableValue();

    @Query("SELECT COALESCE(SUM(c.approvedPayoutAmount), 0.0) FROM LivestockClaim c WHERE c.claimStatus IN ('APPROVED', 'SETTLED')")
    Double sumTotalApprovedPayout();

    @Query("SELECT COALESCE(SUM(c.approvedPayoutAmount), 0.0) FROM LivestockClaim c WHERE c.claimStatus = 'SETTLED'")
    Double sumTotalSettledPayout();
}

