package com.emtech.dairyapp.intergrations.mifugo.claim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimsDashboardMetrics {
    private long totalClaims;
    private long submittedClaims;
    private long underAssessmentClaims;
    private long vetAssessedClaims;
    private long approvedClaims;
    private long settledClaims;
    private long rejectedClaims;
    private double totalInsurableValueKes;
    private double totalApprovedPayoutKes;
    private double totalDisbursedPayoutKes;
    private double biometricMatchRatePercent;
}

