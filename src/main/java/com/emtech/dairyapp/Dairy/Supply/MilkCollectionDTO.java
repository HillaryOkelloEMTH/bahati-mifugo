package com.emtech.dairyapp.Dairy.Supply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MilkCollectionDTO {
    private Long id;
    private Date date;
    private String event;
    private String session;
    private String collector;
    private String pickUpLocation;
    private Double amount;
    private String ward;
    private String route;
    private Integer farmer_no;
    private Double quantity;
    private String last_name;
    private String productType;
    private Date collection_date;
    private Double originalQuantity;
    private String canNo;
    private String farmer;
    private Double currentPrice;
    private String collectionCode;
    private String first_name;
    private Character paymentStatus;
    private Long farmerId;
    private Character updateStatus;
}
