package com.emtech.dairyapp.bulksms;

import com.emtech.dairyapp.Notifications.SMS.smsv1.SMSNOtificaionRepo;
import com.emtech.dairyapp.Notifications.SMS.smsv1.SMSNotifications;
import com.emtech.dairyapp.Notifications.SMS.smsv1.SMSService;
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
@RequestMapping("api/v1/bulkSMS")
@Slf4j
public class BulkSMSController {
    @Autowired
    private SMSNOtificaionRepo smsnOtificaionRepo;
    @Autowired
    private SMSService service;

    @PostMapping(path = "bulk")
    public ResponseEntity<?> sendBulkSMSToFarmers(@RequestBody BulkRequest request) throws JSONException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddss");
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
            return ResponseEntity.ok(new MessageResponse("Processed Successfully!" ));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Error Encountered  -" +e.getLocalizedMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    //Fetch Bulk SMS
    @GetMapping("all/bulk")
    public ResponseEntity<?> fetchALlBulkSMS() {
        return new ResponseEntity<>(smsnOtificaionRepo.findByCategory("Bulk"), HttpStatus.OK);
    }

    @GetMapping("findBy")
    public ResponseEntity<?> fetchALlBulkSMSByCode(@RequestParam("bulkCode") String bulkCode) {
        return new ResponseEntity<>(smsnOtificaionRepo.findByBulkCode(bulkCode), HttpStatus.OK);
    }

    @GetMapping("bulkCodes")
    public ResponseEntity<?> fetchALlBulkSMSCodes() {
        List<SMSNotifications> st = smsnOtificaionRepo.getBulkSMSCodes();
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
