package com.emtech.dairyapp.intergrations.mifugo.claim;

import lombok.Data;

@Data
public class AdjudicateClaimDto {
    // Decision: APPROVED, REJECTED, UNDER_INVESTIGATION
    private String decision;
    private Double approvedPayoutAmount;
    private String adjudicationNotes;
    private String rejectionReason;
    private String approvedBy;
}

