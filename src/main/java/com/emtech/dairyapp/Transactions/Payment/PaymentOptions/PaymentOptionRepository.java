package com.emtech.dairyapp.Transactions.Payment.PaymentOptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentOptionRepository extends JpaRepository<PaymentOption, Long> {
    Optional<PaymentOption> findByCode(String code);
    boolean existsByCode(String code);
    List<PaymentOption> findByCategoryId(Long categoryId);
    boolean existsByName(String name);

    List<PaymentOption> findByDeletedFalse();
    List<PaymentOption>findByCategoryIdAndDeletedFalse(Long categoryId);

}

