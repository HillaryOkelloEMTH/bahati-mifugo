package com.emtech.dairyapp.Dairy.Supply;

import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "collections")
public class MilkCollections {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer farmerNo;//farmer no
    @Column(unique = true)
    private String collectionNumber;
    private String canNo;
    private String productType;
    private String event;//buying or selling
    private Double currentPrice;
    private Date collectionDate=new Date();
    private Date postedOn = new Date();
    private Double originalQuantity=0.0;//KG
    private Double quantity=0.0;//KG
    private Double proteinContent=0.0;
    private Double deductedWeight=0.0;
    private Double fatContent=0.0;
    private String longitude;
    private String latitude;
    private String session;
    private String phoneNo;
    private Character returned=CONSTANTS.NO;
    private String remarks;
    private Double amount;
    @Column(nullable = false)
    private Long collectorId;
    private Long routeFk;
    private  Character status= CONSTANTS.NO;
    private  Character updatedStatus= CONSTANTS.NO;
    private  Date updatedDate;
    private Character paymentStatus=CONSTANTS.NO;
}
