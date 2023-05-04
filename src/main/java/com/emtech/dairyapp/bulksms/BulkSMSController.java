package com.emtech.dairyapp.bulksms;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
@RequestMapping("api/v1/bulkSMS")
public class BulkSMSController {
    @PostMapping(path = "bulk")
    public ResponseEntity<?> sendBulkSMSToFarmers(@RequestBody BulkRequest request) throws JSONException {
        try {
            String body = request.getTemplateBody();
            List<RecipientsItem> recipients = request.getRecipients();
            if(recipients.size() > 0)
            {
                for (RecipientsItem it : recipients) {
                    if(stringIsPhoneNumber(it.getPhoneNumber()))
                    {
                            String name = it.getName();
                            String memberNumber = it.getMemberNumber();
                            String idNumber = it.getIdNumber();
                            body = body.replace("[name]", name).replace("[memberNumber]", memberNumber).replace("[idNumber]",idNumber);
                            System.out.println(body);
                    }
                    else
                    {
                        System.out.println("Phone Number - "+it.getPhoneNumber()+" is invalid!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(new MessageResponse("Response - "+new Date()));
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
