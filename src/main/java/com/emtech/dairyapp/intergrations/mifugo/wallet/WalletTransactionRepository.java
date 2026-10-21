package com.emtech.dairyapp.intergrations.mifugo.wallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWalletIdOrderByTransactionDateDesc(Long walletId);

    List<WalletTransaction> findByFarmerNationalIdOrderByTransactionDateDesc(String nationalId);

    @Query("SELECT t FROM WalletTransaction t WHERE t.farmerNationalId = :nationalId OR (t.farmerNo = :farmerNo AND :farmerNo IS NOT NULL) ORDER BY t.transactionDate DESC")
    List<WalletTransaction> findByFarmerNationalIdOrFarmerNoOrderByTransactionDateDesc(
            @Param("nationalId") String nationalId,
            @Param("farmerNo") Integer farmerNo);

    Optional<WalletTransaction> findByTransactionReference(String reference);

    Optional<WalletTransaction> findBySourceReference(String sourceReference);
}

