package com.emtech.dairyapp.Analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CooperativeDashboardDto {
    // Milk Collection KPIs
    private Double todayQuantityKg;
    private Long todayCollectionsCount;
    private Double totalMilkQuantityKg;
    private Double totalMilkAmountKes;
    private Long activeSupplyingFarmersCount;
    private Long totalRegisteredFarmersCount;

    // Animal Herd KPIs
    private Long totalCattleHerd;
    private Long lactatingCowsCount;
    private Long dryCowsCount;
    private Long sickCowsCount;
    private Double vaccinationComplianceRate;
    private Double totalDailyEstimatedYieldKg;

    // Cooperative Insurance Standing (Summary badge)
    private Long totalClaimsLodged;
    private Long claimsApprovedOrSettled;
    private Long claimsPendingVerification;
    private Double totalClaimPayoutsReceivedKes;

    // Visual Charts Breakdowns (100% dynamic from DB)
    private List<Map<String, Object>> dailyMilkTrend;
    private Map<String, Long> breedDistribution;
    private Map<String, Long> herdStatusDistribution;
    private List<Map<String, Object>> locationIntake;
    private List<Map<String, Object>> recentDeliveries;
    private List<Map<String, Object>> recentLodgedClaims;
}

