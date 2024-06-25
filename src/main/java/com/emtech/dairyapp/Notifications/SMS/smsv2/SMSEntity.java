
package com.emtech.dairyapp.Notifications.SMS.smsv2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SMSEntity {

    private Double credit_balance;
    private Double message_cost;
    private Long message_id;
    private String mobile_number;
    private String network_id;
    private String status_code;
    private String status_desc;

}
