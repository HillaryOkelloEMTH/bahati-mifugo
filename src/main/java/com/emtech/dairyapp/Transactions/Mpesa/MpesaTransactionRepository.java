package com.emtech.dairyapp.Transactions.Mpesa;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MpesaTransactionRepository extends JpaRepository<MpesaSTKTransaction, Long> {
    Optional<MpesaSTKTransaction> findByMerchantRequestID(@NonNull String merchantRequestId);
}
