package com.emtech.dairyapp.Notifications.SMS.smsv1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSResponse {
    private int responseCode;
    private String messageId;
}
