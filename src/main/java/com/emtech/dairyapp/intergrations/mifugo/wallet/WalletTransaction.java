package com.emtech.dairyapp.intergrations.mifugo.wallet;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionReference; // e.g. WTX-20261012-9921

    private Long walletId;
    private Integer farmerNo;
    private String farmerNationalId;

    private Double amount = 0.0;

    // Transaction Type: CLAIM_INDEMNITY_PAYOUT, MILK_PAYMENT_CREDIT, MPESA_WITHDRAWAL, BANK_TRANSFER, COOP_CHECKOFF_DEDUCTION
    private String transactionType;

    // Direction: CREDIT, DEBIT
    private String direction;

    private Double previousBalance = 0.0;
    private Double runningBalance = 0.0;

    private String sourceReference; // e.g. Claim Ref OM-CLM-..., Batch Ref
    private String description;

    // Maker-Checker & Audit
    private String initiatedBy;
    private String approvedBy;
    private String channel = "SYSTEM_AUTOMATED";

    // Status: COMPLETED, PENDING, REVERSED
    private String status = "COMPLETED";

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;
}

