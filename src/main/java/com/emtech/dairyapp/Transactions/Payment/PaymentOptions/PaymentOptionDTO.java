package com.emtech.dairyapp.Transactions.Payment.PaymentOptions;

import lombok.Data;

import java.util.Date;

@Data
public class PaymentOptionDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private boolean active;
    private Long categoryId;
    private String categoryName;
    private Date createdOn;
}

