package com.emtech.dairyapp.Transactions.Payment.PaymentOptions;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentCategoryDTO {
    private Long id;
    private String name;
    private boolean active;
    private  LocalDate createdAt;
    private boolean deleted;
}

