package com.emtech.dairyapp.intergrations.mifugo.animal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoopHerdMetricsDto {
    private long totalCattle;
    private long lactatingCount;
    private long dryCount;
    private long sickCount;
    private long deceasedCount;
    private long vaccinatedCount;
    private long scheduledVaccinationsCount;
    private long overdueVaccinationsCount;
    private double vaccinationComplianceRate;
    private double totalDailyYieldKg;
    private Map<String, Long> breedDistribution;
    private Map<String, Long> statusDistribution;
}

