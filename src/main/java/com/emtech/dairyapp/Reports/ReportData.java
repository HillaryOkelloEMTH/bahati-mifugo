package com.emtech.dairyapp.Reports;

import java.util.Date;

public interface ReportData {

    Double getAmount();
    Double getQuantity();
    Double getCurrent_price();
    Date getdate();
    String getSession();
    String getCollection_number();
    String getCollector();
    String getPickUpLocation();


}
