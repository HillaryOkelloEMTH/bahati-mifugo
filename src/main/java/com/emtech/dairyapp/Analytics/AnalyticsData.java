package com.emtech.dairyapp.Analytics;

import java.util.Date;

public interface AnalyticsData {

    Double getAmount();
    Double getQuantity();
    String getCollector();//collector
    Integer getColectionsCount();
    String getMonth();
    String getRoute();
    String getName();
    String getDate();
    String getLocation();
    String getSession();
    Integer getDayOfMonth();
    Double getPrice();




}
