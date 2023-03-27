package com.emtech.dairyapp.Dairy.Interface;

import java.util.Date;

public interface Statement {

    String getUsername();
    Double getQuantity();
    Double getCurrent_price();
    Double getAmount();
    Date getDate();
    String getDeliveryNumber();
    String getUser_name();
    String getLocations();

}
