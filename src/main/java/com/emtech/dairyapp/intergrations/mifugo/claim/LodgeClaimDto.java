package com.emtech.dairyapp.intergrations.mifugo.claim;

import lombok.Data;

@Data
public class LodgeClaimDto {
    private String farmerNationalId;
    private String animalId;
    private String animalTag;
    private String animalName;
    private String animalBreed;
    private String policyNumber;

    // Cause of loss: MORTALITY_DISEASE, MORTALITY_ACCIDENT, THEFT_AND_STRAY, EMERGENCY_SLAUGHTER, PERMANENT_DISABILITY
    private String claimType;
    private String incidentDate;
    private String incidentLocation;
    private String incidentDescription;

    // Optional initial vet details if already available
    private String vetName;
    private String vetKvbNumber;
    private String vetPhone;
    private String clinicalDiagnosis;
    private String carcassDisposalMethod;
    private Double salvageValue;

    // Optional post-mortem muzzle image key if captured on-site
    private String postMortemMuzzleKey;

    // Financial overrides if custom sum insured
    private Double sumInsured;
    private Double deductibleRate;

    private String reportedBy;
}

