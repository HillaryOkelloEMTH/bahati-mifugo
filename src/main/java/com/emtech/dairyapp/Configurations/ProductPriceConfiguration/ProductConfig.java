package com.emtech.dairyapp.Configurations.ProductPriceConfiguration;


import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductConfig {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String  productName;
    private Character status= CONSTANTS.NO;
    private Double buyingPrice;
    private Double sellingPrice;
    private String unitMeasurement;
    private Integer quantity;
    private Long routeFk;
    private Date createdDate=new Date();
    private Date modifiedDate=new Date();
    private Date effectiveFrom;
}
