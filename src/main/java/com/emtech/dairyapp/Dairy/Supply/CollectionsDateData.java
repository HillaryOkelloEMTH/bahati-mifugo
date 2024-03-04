package com.emtech.dairyapp.Dairy.Supply;


import com.emtech.dairyapp.Dairy.Interface.CurrentTotalCollections;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CollectionsDateData {
    private List<CurrentTotalCollections> totalCollections;
}
