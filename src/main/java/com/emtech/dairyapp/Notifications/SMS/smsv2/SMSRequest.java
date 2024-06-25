package com.emtech.dairyapp.Notifications.SMS.smsv2;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SMSRequest {
    private String api_key;
    private Integer service_id;
    private String mobile;
    private String response_type;
    private String shortcode;
    private String message;
}
