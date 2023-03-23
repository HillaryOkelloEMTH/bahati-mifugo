package com.emtech.dairyapp.Notifications.SMS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class SMSNotifications {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String messageId;
    private String phoneNumber;
    private String senderId;
    private String message;
    private Date sentDate;
    private String eventType;
    private String messageRef;
    private String deliveryTime;
    private String status;
    private String statusReason;
    private String statusDescription;
    private int responseCode;
}
