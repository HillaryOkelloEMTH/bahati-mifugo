package com.emtech.dairyapp.Dairy.Interface;

import java.util.Date;

public interface CollectionsData {
   Long getFarmerId();
    String getCollector();
    String getCanNo();
    String getFarmer();

    Double getAmount();
    Double getQuantity();
    Double getOriginalQuantity();
    Date getCollection_date();
    String getWard();
    String getSession();
    String getPickUpLocation();

    Long getId();
    String getEvent();
    Double getCurrentPrice();
    String getProductType();
    Character getpaymentStatus();
    String getCollectionCode();
    String getFirst_name();
    String getLast_name();
    Integer getFarmer_no();
 String getRoute();
 String getDate();




}
