package com.emtech.dairyapp.Stock.Product.bulk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BulkProductDto {
    private String name;

    private String description;

    private Double price;

    private Double salePrice;

    private Integer stock;

    private String type;

    private Long category;

    private String priceType;
}
