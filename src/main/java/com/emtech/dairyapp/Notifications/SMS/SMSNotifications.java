//package com.emtech.dairyapp.Notifications.SMS;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.ToString;
//
//import jakarta.persistence.*;
//import java.util.Date;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@ToString
//@Entity
//public class SMSNotifications {
//    @Id
//    @GeneratedValue(strategy= GenerationType.IDENTITY)
//    @Column(nullable = false, updatable = false)
//    private Long id;
//
//    private String messageId;
//    private String phoneNumber;
//    private String farmerName;
//    private String senderId;
//    @Column(length = 5000)
//    private String message;
//    private Date sentDate;
//    private String eventType;
//    private String messageRef;
//    private String deliveryTime;
//    private String status;
//    private String statusReason;
//    private String origin;
//    private String statusDescription;
//    private int responseCode;
//    private String category = "One";
//    private String bulkCode="NA";
//    private String smsTemplate="NA";
//}
