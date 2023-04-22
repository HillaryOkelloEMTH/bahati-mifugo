package com.emtech.dairyapp.Dairy.ProductAllocations;

import io.swagger.models.auth.In;

import java.util.Date;

public interface FarmerProducts {


    String getProduct();
    String getFarmer_no();
    Double getAmount();
    Double getQuantity();
    Character getStatus();
    Date getHeat_start_date();
    Integer getNoOfCows();
    String getType();
    String getAllocationDate();


}
