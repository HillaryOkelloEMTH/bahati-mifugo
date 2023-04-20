package com.emtech.dairyapp.Dairy.Supply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateMilkCollectiorequest {
    private String canNo;
    private String collectionNumber;
    private Double originalQuantity=0.0;//KG

}
