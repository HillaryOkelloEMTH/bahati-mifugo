package com.emtech.dairyapp.Dairy.Interface;

import com.emtech.dairyapp.Auth.Utilities.RequestStatus;

public interface AllocationDataInterface {
    Integer getFarmer_no();
    String getFarmer();
    String getRequested_on();
    String getApproval_date();
    String getProduct();
    Integer getQuantity();
    Double getAmount();
    RequestStatus getStatus();
    String getMcc();

}
