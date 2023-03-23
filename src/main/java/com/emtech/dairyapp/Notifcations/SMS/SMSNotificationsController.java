package com.emtech.dairyapp.Notifcations.SMS;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("api/v1/sms")
public class SMSNotificationsController {

    @Autowired
    private SMSNOtificaionRepo smsnOtificaionRepo;


//    @GetMapping("smsCallbacks")
//    public ResponseEntity<?> getSMSCallback(@RequestBody Object object){
//        log.info("Receiving sms callback  at "+ LocalDate.now() + " ....");
//        System.out.println(object.toString());
//        return ResponseEntity.ok().body(object);
//    }

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
            SMSNotifications sn = sms.get();
            sn.setStatus(status);
            sn.setStatusReason(statusreason);
            sn.setStatusDescription(statusdesc);
            sn.setEventType(details.getEvent_type());
            sn.setDeliveryTime(details.getCreated_at());
            smsnOtificaionRepo.save(sn);
        }
    }






}
