
package com.emtech.dairyapp.Notifications.SMS.smsv2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SMSCallback {
    private String destaddr;
    private String dlrDesc;
    private String dlrStatus;
    private String dlrTime;
    private String messageId;
    private String network;
    private String origin;
    private String sourceaddr;
    private Long tat;
}
