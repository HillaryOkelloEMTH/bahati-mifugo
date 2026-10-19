package com.emtech.dairyapp.intergrations.mifugo.wallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWalletIdOrderByTransactionDateDesc(Long walletId);

    List<WalletTransaction> findByFarmerNationalIdOrderByTransactionDateDesc(String nationalId);

    Optional<WalletTransaction> findByTransactionReference(String reference);

    Optional<WalletTransaction> findBySourceReference(String sourceReference);
}

