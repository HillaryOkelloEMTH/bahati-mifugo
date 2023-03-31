package com.emtech.dairyapp.Configurations.ProductPriceConfiguration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PriceChangeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double newPrice;
    private Double oldPrice;
    private Date modifiedDate;
    private String modifiedBy;
    private Long productConfigId;
    private String productName;
    @Column(nullable = false)
    private Long routeFk;


}
