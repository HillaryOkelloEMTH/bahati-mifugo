package com.emtech.dairyapp.Dairy.PaymentComponent;

public interface PaymentFileData {

    String getFarmer_no();
    String getPayment_mode();
    String getUsername();
    Double getCollectionAmount();
    Double getAllocationAmount();
    Double getNetPay();
    Double getAmountPaid();
    String getFreequency();
}
