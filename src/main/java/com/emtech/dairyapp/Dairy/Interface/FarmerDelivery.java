package com.emtech.dairyapp.Dairy.Interface;

import java.util.Date;

public interface FarmerDelivery {
    Double getQuantity();
    Date getDate();
    Double getAmount();
    String getCollector();
    Character getPaid();
    String getSession();
}
