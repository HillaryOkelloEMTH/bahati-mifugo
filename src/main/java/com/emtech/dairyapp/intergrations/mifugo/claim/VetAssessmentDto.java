package com.emtech.dairyapp.intergrations.mifugo.claim;

import lombok.Data;

@Data
public class VetAssessmentDto {
    private String vetName;
    private String vetKvbNumber;
    private String vetPhone;
    private String clinicalDiagnosis;
    // Carcass disposal: BURIAL, INCINERATION, SALVAGE_MEAT, PENDING_DISPOSAL
    private String carcassDisposalMethod;
    private Double salvageValue;
    // Recommendation: RECOMMEND_PAYOUT, RECOMMEND_REPUDIATION, FURTHER_LAB_TESTS_REQUIRED
    private String vetRecommendation;
    private String vetNotes;
    private String postMortemMuzzleKey;
    private String assessedBy;
}

