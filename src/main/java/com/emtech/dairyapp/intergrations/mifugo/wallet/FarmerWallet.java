package com.emtech.dairyapp.intergrations.mifugo.wallet;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "farmer_wallets")
public class FarmerWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String walletAccountNumber; // e.g. WAL-BHT-24891024

    private Integer farmerNo;

    @Column(unique = true, nullable = false)
    private String farmerNationalId;

    private String farmerName;
    private String phoneNumber;

    private Double balance = 0.0;
    private Double totalDisbursedClaims = 0.0;
    private Double totalMilkEarnings = 0.0;
    private Double totalWithdrawn = 0.0;

    private String currency = "KES";
    private String status = "ACTIVE"; // ACTIVE, FROZEN, SUSPENDED

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastTransactionDate;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}

