package com.emtech.dairyapp.intergrations.mifugo.animal;

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
@Table(name = "livestock_animals")
public class LivestockAnimal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String animalTag; // e.g. BF-084, BF-112, COW-001

    private String animalName;
    private String breed; // Friesian, Ayrshire, Jersey, Guernsey, Boran, Sahiwal
    private String species = "Cattle";
    private String gender = "Female";

    // Pedigree / Birth Details
    private String dateOfBirth; // yyyy-MM-dd
    private Double birthWeightKg;
    private String sireTag; // Father
    private String damTag;  // Mother
    private String colorMarkings;
    private String origin = "Born on Farm"; // Born on Farm, Purchased, Gifted

    // Farmer Ownership Linkage
    private Integer farmerNo;
    private String farmerNationalId;
    private String farmerName;
    private String farmerPhone;
    private String cooperativeName = "Bahati Dairy Farmers Co-operative";

    // Current Status: HEALTHY, LACTATING, DRY, SICK, QUARANTINE, DECEASED, SLAUGHTERED
    private String status = "HEALTHY";

    // Lactation & Production Metrics
    private Integer lactationStage = 1; // 1st lactation, 2nd, etc.
    private String lactationStatus = "LACTATING"; // LACTATING, DRY_PERIOD, HEIFER, CALF
    private Double dailyAverageYieldKg = 0.0;
    private Double totalCumulativeYieldKg = 0.0;
    private String lastCalvingDate;

    // Biometric Muzzle Enrollment
    private String muzzleBiometricKey;
    private Boolean isMuzzleEnrolled = false;
    private Boolean isMuzzleVerified = false;
    private Double biometricConfidenceScore = 0.0;
    private String muzzleEnrollmentDate;

    // Insurance Standing
    private String policyNumber;
    private Double insurableValueKes = 0.0;
    private Double sumInsuredKes = 0.0;
    private String insuranceStatus = "ACTIVE_COVER"; // ACTIVE_COVER, PENDING_INSPECTION, CLAIM_FILED, SETTLED

    // Mortality / Death particulars (if deceased)
    private String dateOfDeath;
    private String causeOfDeath;
    private String deathLocation;
    private String carcassDisposalMethod; // BURIAL, INCINERATION, SALVAGE_MEAT
    private String postMortemNotes;
    private String attendingVetName;
    private String attendingVetKvb;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}

