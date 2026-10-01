package com.emtech.dairyapp.Notifications.SMS.smsv2;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SMSResponse {
    private String status_code;
    private String status_desc;
    private Integer message_id;
    private String mobile_number;
    private String network_id;
    private Double message_cost;
    private Double credit_balance;
    }
