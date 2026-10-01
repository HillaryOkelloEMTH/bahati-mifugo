package com.emtech.dairyapp.Dairy.Interface;

import java.util.Date;

public interface PurchaseData {
    String getCollector();
    Double getAmount();
    Double getQuantity();
    Date getCollection_date();
    Long getId();
    String getEvent();
    Double getCurrentPrice();
    String getProductType();
    Character getpaymentStatus();
    String getCollectionCode();
    String getPhone_no();

}
