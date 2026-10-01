package com.emtech.dairyapp.Dairy.FloatTracking;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FloatAllocationRequest {

    private Long collectorId;
    private Double allocationAmount;
    private String allocateBy;
    private String mode;//cash/mpesa


}
