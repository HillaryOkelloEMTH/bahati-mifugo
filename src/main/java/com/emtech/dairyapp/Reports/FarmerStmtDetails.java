package com.emtech.dairyapp.Reports;

import java.util.Date;

public interface FarmerStmtDetails {


    String getDeliveryNo();
    Double getQuantity();
    Date getCollection_date();
    String getSession();
}
