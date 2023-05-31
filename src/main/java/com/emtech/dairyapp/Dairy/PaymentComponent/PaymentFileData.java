package com.emtech.dairyapp.Dairy.PaymentComponent;

public interface PaymentFileData {

    String getFarmer_no();
    String getPayment_mode();
    String getUsername();
    String getMobile_no();
    String getBranch();
    String getAccount_number();
    String getAccount_name();
    Double getCollectionAmount();
    Double getAllocationAmount();
    Double getNetPay();
    Double getAmountPaid();
    String getFreequency();
}
