package com.emtech.dairyapp.Dairy.Interface;

import java.util.Date;

public interface CollectionItemData {
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
    String getCollectionCode();
    String getFirst_name();
    String getLast_name();
    Integer getFarmer_no();

    String getMobile_no();

    String getCan_no();

    String getSession();
  String getLatitude();
  String getLongitude();
  String getRoute_fk();
 String getRoute();
 String getDate();




}
