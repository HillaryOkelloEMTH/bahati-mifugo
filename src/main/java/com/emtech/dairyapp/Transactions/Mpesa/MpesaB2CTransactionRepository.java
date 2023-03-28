package com.emtech.dairyapp.Transactions.Mpesa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MpesaB2CTransactionRepository extends JpaRepository<MpesaB2CTransaction, Long> {
}
