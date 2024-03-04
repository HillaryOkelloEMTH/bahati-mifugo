package com.emtech.dairyapp.Dairy.Supply;


import com.emtech.dairyapp.Dairy.Interface.CurrentTotalCollections;
import com.emtech.dairyapp.Dairy.Interface.CurrentTotalFarmers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalCollectionsFarmers {
    private List<CurrentTotalCollections> totalCollections;
    private List<CurrentTotalFarmers> totalFarmers;
}
