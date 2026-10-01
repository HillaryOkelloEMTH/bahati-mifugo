package com.emtech.dairyapp.Configurations.MccProductPrices;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double buyingPrice;
    private double sellingPrice;
    private Date createdOn = new Date();
    private Date updatedOn = new Date();
    private Date effectiveFrom;
    private Long productId;
    private Long locationId;
}
