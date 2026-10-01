package com.emtech.dairyapp.Dairy.ProductAllocations.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceApplicationUpdate {
    private Long serviceId;
    private String resolvedBy;
    private String comments;
    private String status;
}
