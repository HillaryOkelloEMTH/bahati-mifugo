package com.emtech.dairyapp.Notifications.SMS.smsv1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SMSCallback {
    @JsonProperty("actor_type")
    private String actor_type;
    @JsonProperty("created_at")
    private String created_at;
    @JsonProperty("data")
    private SMSData data;
    @JsonProperty("event_type")
    private String event_type;
    @JsonProperty("uid")
    private String uid;
}
