package com.emtech.dairyapp.intergrations.mifugo.claim;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "livestock_claims")
public class LivestockClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String claimReference;

    private String policyNumber;
    private Long farmerId;
    private Integer farmerNo;
    private String farmerNationalId;
    private String farmerName;
    private String farmerPhone;
    private String farmerLocation;

    // Animal particulars
    private String animalId;
    private String animalTag;
    private String animalName;
    private String animalBreed;
    private String registeredMuzzleKey;

    // Cause of loss / Claim type:
    // MORTALITY_DISEASE, MORTALITY_ACCIDENT, THEFT_AND_STRAY, EMERGENCY_SLAUGHTER, PERMANENT_DISABILITY
    private String claimType;
    private String incidentDate;
    private String incidentLocation;

    @Column(columnDefinition = "TEXT")
    private String incidentDescription;

    @Temporal(TemporalType.TIMESTAMP)
    private Date notificationDate;

    // Veterinary Assessment details
    private String vetName;
    private String vetKvbNumber;
    private String vetPhone;

    @Column(columnDefinition = "TEXT")
    private String clinicalDiagnosis;

    // Carcass disposal: BURIAL, INCINERATION, SALVAGE_MEAT, PENDING_DISPOSAL
    private String carcassDisposalMethod;
    private Double salvageValue = 0.0;
    private String vetRecommendation;

    @Column(columnDefinition = "TEXT")
    private String vetNotes;

    // Post-Mortem Biometric Muzzle Verification
    private String postMortemMuzzleKey;
    private Double muzzleBiometricMatchScore = 0.0;
    // Biometric status: VERIFIED, UNVERIFIED, FLAGGED_MISMATCH
    private String biometricStatus;

    // Bahati Dairy cross-validation
    private Double thirtyDayMilkKg = 0.0;
    private String lastDeliveryDate;
    private String dairyDeliveryStatus;

    // Financial / Underwriting values
    private Double sumInsured = 0.0;
    private Double deductibleRate = 0.10;
    private Double deductibleAmount = 0.0;
    private Double netPayableAmount = 0.0;
    private Double approvedPayoutAmount = 0.0;

    // Claim Workflow Status:
    // SUBMITTED, UNDER_INVESTIGATION, VET_ASSESSED, APPROVED, REJECTED, SETTLED
    private String claimStatus;

    // Underwriting / Adjudication
    private String adjudicationDecision;
    @Column(columnDefinition = "TEXT")
    private String adjudicationNotes;
    private String rejectionReason;

    // Settlement details
    // Payment methods: MPESA_B2C, BANK_TRANSFER, DAIRY_ACCOUNT_CREDIT
    private String settlementMethod;
    private String settlementReference;

    @Temporal(TemporalType.TIMESTAMP)
    private Date settlementDate;
    private String payeeAccount;

    // Audit actors
    private String reportedBy;
    private String assessedBy;
    private String approvedBy;
    private String settledBy;

    @Column(columnDefinition = "TEXT")
    private String auditTrailJson;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}

