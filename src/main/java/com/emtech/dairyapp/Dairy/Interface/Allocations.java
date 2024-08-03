package com.emtech.dairyapp.Dairy.Interface;

import com.emtech.dairyapp.Auth.Utilities.RequestStatus;

import java.util.Date;

public interface Allocations {

    Long getId();
    String getProduct();
    String getUsername();
    RequestStatus getStatus();
    String getType();
    Integer getNoOfCows();
    Date getheatStartDate();

    Integer getFarmer_no();
    Double getAmount();
    Integer getQuantity();
    Date getAllocationDate();
    Date getRequestedOn();

    String getComments();
    String getTime();
    String getAllocatedBy();
    Date getApprovalDate();
    Character getPaymentStatus();
    Character getRevokeStatus();
}
