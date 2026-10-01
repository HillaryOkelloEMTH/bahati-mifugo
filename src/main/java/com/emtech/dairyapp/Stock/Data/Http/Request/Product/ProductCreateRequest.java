package com.emtech.dairyapp.Stock.Data.Http.Request.Product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import jakarta.persistence.Column;
import java.io.Serializable;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequest implements Serializable {
    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "price")
    private Double price;

    @JsonProperty(value = "salePrice")
    private Double salePrice;

    @JsonProperty(value = "stock")
    private Integer stock;
    @JsonProperty(value = "type")
    private String type;

    @JsonProperty(value = "category")
    private Long category;

    @JsonProperty(value = "priceType")
    private String priceType;


}
