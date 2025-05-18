package com.emtech.dairyapp.Notifications.SMS.smsv2;

import lombok.Data;

@Data
public class SmsReqDto {
    String message, phoneNumber, bulkTemplate, bulkCode;
    boolean bulk;

}
