package com.emtech.dairyapp.Dairy.Interface;

import java.util.Date;

public interface Allocations {

    Long getId();
    String getProduct();
    String getUsername();
    Integer getFarmer_no();
    Double getAmount();
    Double getQuantity();
    Date getAllocationDate();
    String getTime();
    String getAllocatedBy();
    Character getPaymentStatus();
    Character getRevokeStatus();

}
