package com.emtech.dairyapp.Transactions.Payment.PaymentOptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentCategoryRepository extends JpaRepository<PaymentCategory, Long> {
    Optional<PaymentCategory> findByName(String name);
    boolean existsByName(String name);
}

