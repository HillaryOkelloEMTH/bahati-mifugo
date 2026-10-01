package com.emtech.dairyapp.Dairy.ProductAllocations.dto;

import lombok.Data;

@Data
public class ProductRequestDto {
    private Integer farmerNo;
    private String farmerName;
    private Long locationId;
    private Long routeFk;
    private Long productId;
    private Integer quantity;
    private Double amount;
    private Double price;
    private String productName;
    private String type;
    private String comments;
}
