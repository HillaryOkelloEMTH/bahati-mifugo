package com.emtech.dairyapp.Configurations.Interfaces;

import com.emtech.dairyapp.Configurations.FarmerManagement.Farmer;

public interface FarmerInfo {

    Integer getNo_of_cows();
    Long getId();
    Integer getFarmer_no();
    String getSubcounty();

    String getName();
    String getCounty();
    String getMember_type();
    String getMobile_no();
    Long getRouteId();
    Long getLocationId();

    String getDeleted_flag();
    String getCreated_at();
    String getId_number();
    String getRoute();

    String getLast_name();
    String getPayment_freequency();
    String getAlternative_mobile_no();
    String getPickUpLocation();
    String getAccount_name();
    String getAccount_number();
    String getPayment_mode();

    String getUsername();



}
