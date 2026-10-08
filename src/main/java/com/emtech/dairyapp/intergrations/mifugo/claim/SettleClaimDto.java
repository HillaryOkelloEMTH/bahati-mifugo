package com.emtech.dairyapp.intergrations.mifugo.claim;

import lombok.Data;

@Data
public class SettleClaimDto {
    // Payment method: MPESA_B2C, BANK_TRANSFER, DAIRY_ACCOUNT_CREDIT
    private String settlementMethod;
    private String settlementReference;
    private String payeeAccount;
    private Double amountPaid;
    private String settledBy;
    private String settlementNotes;
}

