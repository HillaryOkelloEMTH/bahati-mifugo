package com.emtech.dairyapp.Dairy.Interface;

import java.util.Date;

public interface Allocations {

    Long getId();
    String getProduct();
    String getUsername();
    Character getStatus();
    String getType();
    Integer getNoOfCows();
    Date getheatStartDate();

    Integer getFarmer_no();
    Double getAmount();
    Double getQuantity();
    Date getAllocationDate();
    String getTime();
    String getAllocatedBy();
    Character getPaymentStatus();
    Character getRevokeStatus();

}
