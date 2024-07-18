package com.emtech.dairyapp.Dairy.ProductAllocations;


import com.emtech.dairyapp.Auth.Utilities.RequestStatus;
import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Timestamp;
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
    private String farmerName;
    private Integer farmerNo;//farmer no
    private Long productId;
    private String productName;
    private Date allocationDate;
    private Date approvalDate;
    private String allocatedBY;
    private Integer quantity;
    private String type;//Good/Service
    private Double amount;
    private Date heatStartDate;
    private Integer noOfCows;

    @Enumerated(EnumType.STRING)
    private RequestStatus status= RequestStatus.PENDING;
    private Double productPrice;
    private Character revokeStatus=CONSTANTS.NO;
    private Character paymentStatus= CONSTANTS.NO;
    private Long locationId;

    //Service properties
    private String serviceStatus; //Pending, Closed, Cancelled
    private Date requestedOn = new Date();
    private Date resolvedOn;
    private String resolvedBy;
    private String comments;
}
