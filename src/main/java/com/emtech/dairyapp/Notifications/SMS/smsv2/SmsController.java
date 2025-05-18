package com.emtech.dairyapp.Notifications.SMS.smsv2;


import com.emtech.dairyapp.Notifications.SMS.smsv1.SmsNotificationRepo;
import com.emtech.dairyapp.Notifications.SMS.smsv1.SMSNotifications;
import com.emtech.dairyapp.Response.EntityResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("api/v1/sms-notifications")
public class SmsController {

    @Autowired
    private SmsServiceV2 smsServiceV2;
    @Autowired
    private SmsNotificationRepo smsNotificationRepo;

    @PostMapping("send/notification")
    public Mono<ResponseEntity<?>> sendSmsNotification(@RequestParam String message, @RequestParam String mobile){
        EntityResponse<Object> response = new EntityResponse<>();

        return smsServiceV2.sendSMSNotification(message, mobile)
                .doOnSuccess(smsResponse -> {
                    response.setMessage("Sent Successfully");
                    response.setEntity(smsResponse);
                    response.setStatusCode(HttpStatus.OK.value());
                }
                )
                .doOnError(error -> {
                    response.setMessage("Failed to send SMS");
                    response.setEntity(null);
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    log.error("Error sending SMS: {}", error.getMessage());
                })
                .thenReturn(ResponseEntity.ok().body(response));
    }

    @PostMapping("/callback")
    public void  receiveCallback(@RequestBody SMSCallback details){
        Gson gs = new Gson();
        log.info("Tilil SMS Callback Received {}. The body is", gs.toJson(details));
        String status = details.getDlrStatus();
        String origin = details.getOrigin();
        String statusDesc = details.getDlrDesc();
        String messageId = details.getMessageId();

        //Update and status description in SMS Notifications Table
        Optional<SMSNotifications> sms = smsNotificationRepo.findByMessageId(messageId);
        if (sms.isPresent()) {
            log.info("SMS found");
            log.info("Updating SMS delivery status...");
            SMSNotifications sn = sms.get();
            sn.setStatus(status);
            sn.setOrigin(origin);
            sn.setStatusDescription(statusDesc);
            sn.setDeliveryTime(details.getDlrTime());
            smsNotificationRepo.save(sn);
        }
        log.info("Done updating.");
    }

}
