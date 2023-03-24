package com.emtech.dairyapp.Notifications.SMS;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SMSData {
    @JsonProperty("message_id")
    private String message_id;
    @JsonProperty("message_ref")
    private String message_ref;
    @JsonProperty("status")
    private String status;
    @JsonProperty("status_description")
    private String status_description;
    @JsonProperty("status_reason")
    private String status_reason;
}
