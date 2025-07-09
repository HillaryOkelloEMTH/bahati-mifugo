package com.emtech.dairyapp.Transactions.Payment.PaymentOptions;

import lombok.Data;

@Data
public class PaymentOptionDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private boolean active;
    private Long categoryId;
    private String categoryName;
}

