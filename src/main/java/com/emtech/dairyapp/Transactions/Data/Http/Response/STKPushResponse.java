package com.emtech.dairyapp.Transactions.Data.Http.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class STKPushResponse {
    private String MerchantRequestID;
    private String ResponseCode;
    private String CustomerMessage;
    private String CheckoutRequestID;
    private String ResponseDescription;
}
