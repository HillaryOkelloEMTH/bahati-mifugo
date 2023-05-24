package com.emtech.dairyapp.Dairy.ProductAllocations;


import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FarmerProductAllocations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer farmerNo;//farmer no
    private Long productId;
    private Date allocatioDate;
    private String allocatedBY;
    private Double quantity;
    private String type;//Good/Service
    private Double amount;
    private Date heatStartDate;
    private Integer noOfCows;
    private Character status=CONSTANTS.NO;
    private Double productPrice;
    private Character revokeStatus=CONSTANTS.NO;
    private Character paymentStatus= CONSTANTS.NO;

    //Service properties
    private String serviceStatus; //Pending, Closed, Cancelled
    private Date requestedOn;
    private Date resolvedOn;
    private String resolvedBy;









}
