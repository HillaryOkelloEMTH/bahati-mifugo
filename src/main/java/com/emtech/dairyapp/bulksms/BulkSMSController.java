package com.emtech.dairyapp.bulksms;

import com.emtech.dairyapp.Notifications.SMS.smsv1.SmsNotificationRepo;
import com.emtech.dairyapp.Notifications.SMS.smsv1.SMSNotifications;
import com.emtech.dairyapp.Notifications.SMS.smsv1.SMSService;
import com.emtech.dairyapp.Notifications.SMS.smsv2.SmsServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
@RequestMapping("api/v1/bulk-sms")
@Slf4j
public class BulkSMSController {
    @Autowired
    private SmsNotificationRepo smsNotificationRepo;
    @Autowired
    private SMSService service;
    @Autowired
    private SmsServiceV2 serviceV2;

    @PostMapping(path = "bulk")
    public ResponseEntity<?> sendBulkSMSToFarmers(@RequestBody BulkRequest request) throws JSONException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddss");
            String bulkCode = "BULKSMS" + sdf.format(new Date());
            log.info("Total Number of SMS to be sent - {}", request.getRecipients().size());
            log.info("Bulk SMS Code - {}", bulkCode);
            List<RecipientsItem> recipients = request.getRecipients();
            if (!recipients.isEmpty()) {
                for (RecipientsItem recipient : recipients) {
                    if (stringIsPhoneNumber(recipient.getPhoneNumber())) {
                        String body = request.getTemplateBody();
                        body = body.replace("[name]", recipient.getName()).replace("[memberNumber]", recipient.getMemberNumber()).replace("[idNumber]", recipient.getIdNumber());
                        log.info("Sending SMS to - {}", recipient.getPhoneNumber());
                        log.info("Message - {}", body);
                        service.BulkSMSNotification(body, recipient.getPhoneNumber(), bulkCode, request.getTemplateName());
                    } else {
                        System.out.println("Phone Number - " + recipient.getPhoneNumber() + " is invalid!");
                    }
                }
            }
            return ResponseEntity.ok(new MessageResponse("Messages Processed Successfully!" ));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Failed to send messages"),HttpStatus.BAD_REQUEST);
        }
    }

    //Fetch Bulk SMS
    @GetMapping("all/bulk")
    public ResponseEntity<?> fetchALlBulkSMS() {
        return new ResponseEntity<>(smsNotificationRepo.findByCategory("Bulk"), HttpStatus.OK);
    }

    @GetMapping("findBy")
    public ResponseEntity<?> fetchALlBulkSMSByCode(@RequestParam("bulkCode") String bulkCode) {
        return new ResponseEntity<>(smsNotificationRepo.findByBulkCode(bulkCode), HttpStatus.OK);
    }

    @GetMapping("find/date-range")
    public ResponseEntity<?> getByDateRange(@RequestParam String from, @RequestParam String to) {
        var res = serviceV2.getMessagesByDateRange(from, to);
        return new ResponseEntity<>(res, HttpStatus.valueOf(res.getStatusCode()));
    }

    @GetMapping("bulkCodes")
    public ResponseEntity<?> fetchALlBulkSMSCodes() {
        List<SMSNotifications> st = smsNotificationRepo.getBulkSMSCodes();
        List<String> codes = new ArrayList<>();
        for (SMSNotifications s : st) {
            codes.add(s.getBulkCode());
        }
        return new ResponseEntity<>(codes, HttpStatus.OK);
    }

    //Validate Recipient Phone numbers
    public static boolean stringIsPhoneNumber(String phoneno) {
        String phonereg = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";
        Pattern pattern = Pattern.compile(phonereg);
        if (!phoneno.contains("+")) {
            phoneno = "+" + phoneno;
        }
        Matcher matcher = pattern.matcher(phoneno);
        return matcher.matches();
    }
}
