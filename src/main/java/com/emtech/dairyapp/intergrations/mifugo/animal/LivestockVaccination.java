package com.emtech.dairyapp.intergrations.mifugo.animal;

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
@Table(name = "livestock_vaccinations")
public class LivestockVaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String animalTag;

    private Long animalId;
    private String farmerNationalId;

    // Vaccine identity
    private String vaccineName; // Foot and Mouth Disease (FMD), East Coast Fever (ECF - ITM), Anthrax & Blackquarter, Brucellosis, Lumpy Skin Disease (LSD), Rift Valley Fever
    private String targetDisease;
    private String doseAdministered; // e.g. 2ml Subcutaneous, 1ml IM
    private String batchNumber;
    private String manufacturer;

    // Timeline & Animal Age
    private String administeredDate; // yyyy-MM-dd
    private String nextDueDate;      // yyyy-MM-dd (booster date)
    private String animalAgeAtVaccination; // e.g. "4 months", "1 yr 2 mos", "2 yrs 4 mos"

    // Biometric Muzzle Verification Link
    private String muzzleBiometricKey; // Reference to bovine muzzle print
    private String identificationMethod = "MUZZLE_BIOMETRICS"; // MUZZLE_BIOMETRICS, EARTAG_FALLBACK

    // Attending Veterinary Personnel
    private String vetName;
    private String vetKvbNumber; // Kenya Veterinary Board license
    private String vetPhone;

    // Status: COMPLETED, SCHEDULED, OVERDUE
    private String status = "COMPLETED";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}

