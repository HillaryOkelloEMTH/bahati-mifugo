package com.emtech.dairyapp.Dairy.Interface;

import java.util.Date;

public interface CollectionsData {
   Long getFarmerId();
    String getCollector();
    String getFarmer();
    Double getAmount();
    Double getQuantity();
    Date getCollection_date();
    String getWard();
    String getPickUpLocation();
    Long getId();
    String getEvent();
    Double getCurrentPrice();
    String getProductType();
    Character getpaymentStatus();

}
