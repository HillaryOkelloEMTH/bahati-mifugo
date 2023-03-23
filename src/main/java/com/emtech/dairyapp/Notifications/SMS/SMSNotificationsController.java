package com.emtech.dairyapp.Notifications.SMS;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("api/v1/sms")
public class SMSNotificationsController {

    @Autowired
    private SMSNOtificaionRepo smsnOtificaionRepo;
    @Autowired
    private SMSService service;




    @RequestMapping("/smsCallbacks")
    public void receiveSMSCallbacks(@RequestBody SMSCallback details) {
        Gson gs = new Gson();
        log.info("SMS Callback Received { " + gs.toJson(details) + " }");
        String status = details.getData().getStatus();
        String statusreason = details.getData().getStatus_reason();
        String statusdesc = details.getData().getStatus_description();
        String messageId = details.getData().getMessage_id();

        //Update and status description in SMS Notifications Table
        Optional<SMSNotifications> sms = smsnOtificaionRepo.findByMessageId(messageId);
        if (sms.isPresent()) {
            log.info("SMS found");
            log.info("Updating SMS...");
            SMSNotifications sn = sms.get();
            sn.setStatus(status);
            sn.setStatusReason(statusreason);
            sn.setStatusDescription(statusdesc);
            sn.setEventType(details.getEvent_type());
            sn.setDeliveryTime(details.getCreated_at());
            smsnOtificaionRepo.save(sn);
        }
        log.info("Done");
    }
    @RequestMapping("/sendSMS")
    public ResponseEntity<?> sendSSMS(@RequestParam String message,@RequestParam String phone) {
        service.SMSNOtification(message,phone);
      return ResponseEntity.ok().body("Done");
    }





}
