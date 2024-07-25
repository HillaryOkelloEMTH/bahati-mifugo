package com.emtech.dairyapp.Configurations.FarmerManagement;

public interface FarmerInterface {
    String getRouteName();
    Integer getId();
    String getUsername();
    String getFirstName();
    String getLastName();
    String getIdNumber();
    Integer getFarmerNo();
    String getMobileNo();
    String getAlternativeMobileNo();
    String getMemberType();
    String getAddress();
    String getPaymentFrequency();
    String getPaymentDate();
    String getPaymentMode();
    String getCreatedAt();
    String getDeletedFlag();
    String getDeletedOn();
    String getLocation();
    String getSubLocation();
    String getVillage();
    Integer getCountyFk();
    Integer getSubcountyFk();
    Integer getWardFk();
    Integer getNoOfCows();
    Integer getRouteFk();
    String getTransportMeans();
    String getGender();
}
