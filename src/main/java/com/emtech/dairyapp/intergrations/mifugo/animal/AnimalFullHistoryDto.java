package com.emtech.dairyapp.intergrations.mifugo.animal;

import com.emtech.dairyapp.intergrations.mifugo.claim.LivestockClaim;
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
public class AnimalFullHistoryDto {
    private LivestockAnimal animal;
    private List<LivestockVaccination> vaccinations;
    private List<LivestockHealthRecord> healthRecords;
    private List<LivestockClaim> claims;
    private Map<String, Object> milkProductionSummary;
    private Map<String, Object> insuranceValuation;
}

