package com.emtech.dairyapp.intergrations.mifugo.wallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FarmerWalletRepository extends JpaRepository<FarmerWallet, Long> {

    Optional<FarmerWallet> findByFarmerNationalId(String nationalId);

    Optional<FarmerWallet> findByFarmerNo(Integer farmerNo);

    Optional<FarmerWallet> findByWalletAccountNumber(String accountNumber);

    @Query("SELECT COALESCE(SUM(w.balance), 0.0) FROM FarmerWallet w")
    Double sumTotalWalletBalances();

    @Query("SELECT COALESCE(SUM(w.totalDisbursedClaims), 0.0) FROM FarmerWallet w")
    Double sumTotalDisbursedClaims();

    @Query("SELECT COALESCE(SUM(w.totalMilkEarnings), 0.0) FROM FarmerWallet w")
    Double sumTotalMilkEarnings();
}

