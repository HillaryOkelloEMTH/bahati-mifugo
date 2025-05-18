package com.emtech.dairyapp.Notifications.SMS.smsv1;


import com.emtech.dairyapp.Notifications.SMS.smsv2.SmsReqDto;
import com.emtech.dairyapp.Notifications.SMS.smsv2.SmsServiceV2;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
@Slf4j
public class SMSService {

    //Call Back URL
    @Value("${ebs.callbackurl.one}")
//    private String callbackurl;
    private String callbackurl = "http://52.15.152.26:9900/api/v1/sms/smsCallbacks";

    //URL
    @Value("${ebs.url}")
//    private String url;
    private String url = "https://sms.crossgatesolutions.com:18095/v1/bulksms/messages";

    //Message Type
    @Value("${ebs.messagetype}")
//    private String msgtype;
    private String msgtype = "promotional";

    //Profile Code
    @Value("${ebs.profileCode}")
//    private String profileCode;
    private String profileCode = "2208021";

    //API Key
    @Value("${ebs.apiKey}")
//    private String apiKey;
    private String apiKey = "NDU4MThmODAxMzM2ODk3MUlELTQ2MmU4Y2QwZDA4YjQxOGU5ZjZjMTQ0ZGM0MmE4NDY5";

    @Autowired
    private SmsNotificationRepo smsNotificationsRepository;

    @Autowired
    SmsServiceV2 smsServiceV2;

    public static String generatecSystemCode(int len) {
        String chars = "JUFREDDAIRYFARM1234567890";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < 12; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length()))).toString();
        log.info("RANDOM STRING :: " + sb);
        return sb.toString();
    }

    public SMSResponse sendSMS(String message, String phone) {
        String phoneno = phone.trim();
        log.info("Entered Phone number == " + phoneno);
        if (phoneno.startsWith("0")) {
            log.info("Starting with 0");
            phoneno = phoneno.replaceFirst("0", "254");
        } else if (phoneno.startsWith("+")) {
            log.info("Starting with +");
            phoneno = phoneno.substring(1, phoneno.length());
        } else if (phoneno.startsWith("7") || phoneno.startsWith("1")) {
            phoneno = "254" + phoneno;
        }
        log.info("Formated " + phoneno);
        //Time Stamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String messageref = generatecSystemCode(6);

        SMSResponse sr = new SMSResponse();

        String requestJson = "{\"profile_code\": \"" + profileCode + "\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"mobile_number\": \"" + phoneno + "\",\n" +
                "      \"message\": \"" + message + "\",\n" +
                "      \"message_type\": \"" + msgtype + "\",\n" +
                "      \"message_ref\": \"" + messageref + "\"\n" +
                "      \n" +
                "    }\n" +
                "  ],\n" +
                "  \"dlr_callback_url\": \"" + callbackurl + "\"\n" +
                "}";

        log.info("SENDING REQUEST AT " + timestamp + " ");
        log.info("REQUEST TO CROSSGATE Profile Code  { " + profileCode + " } Destination { " + phoneno + " } Message { " + message + " }");

//        OkHttpClient client = null;
//        client = new OkHttpClient.Builder()
//                .connectTimeout(90000, TimeUnit.MILLISECONDS)
//                .readTimeout(90000, TimeUnit.MILLISECONDS)
//                .build();
//
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        //Disable SSL
        String javaHomePath = System.getProperty("java.home");
        String keystore = javaHomePath + "/lib/security/cacerts";
        String storepass = "changeit";
        String storetype = "JKS";

        String[][] props = {
                {"jakarta.net.ssl.trustStore", keystore,},
                {"jakarta.net.ssl.keyStore", keystore,},
                {"jakarta.net.ssl.keyStorePassword", storepass,},
                {"jakarta.net.ssl.keyStoreType", storetype,},
        };
        for (int i = 0; i < props.length; i++) {
            System.getProperties().setProperty(props[i][0], props[i][1]);
        }

        RequestBody body = RequestBody.create(mediaType, requestJson);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("api-key", apiKey)
                .addHeader("cache-control", "no-cache")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            System.out.println(res);
            log.info("RESPONSE BODY FROM CROSSGATE { " + res + " }");
            JSONArray jar = new JSONArray(res);
            JSONObject json = null;
            for (Object obj : jar) {
                json = new JSONObject(obj.toString());
            }

            int code = response.code();

            if (response.isSuccessful()) {
                log.info("RECEIVING RESPONSE AT { " + timestamp + " } CODE { " + code + " }");
                sr.setResponseCode(code);
                sr.setMessageId(json.getString("message_id"));
            } else {
                log.info("ERROR FROM CROSSGATE SMS GATEWAY \n" + res);
                sr.setResponseCode(code);
                sr.setMessageId("-");
            }
        } catch (Exception e) {
            log.info("ERROR WHEN SENDING SMS GATEWAY { " + e.getLocalizedMessage() + " }");
            sr.setResponseCode(1009);
            sr.setMessageId("-");
        }
        return sr;
    }


    public void SMSNOtification(String message, String phoneNumber) {
        //Create Message and Save In DB
        SMSResponse sr = sendSMS(message, phoneNumber);
        SMSNotifications sms = new SMSNotifications();
        sms.setResponseCode(sr.getResponseCode());
        sms.setEventType("-");
        sms.setDeliveryTime("-");
        sms.setMessageRef(generatecSystemCode(10));
        sms.setMessageId(sr.getMessageId());
        sms.setMessage(message);
        sms.setSentDate(new Date());
        sms.setPhoneNumber(phoneNumber);
//        System.out.println(sms);
        smsNotificationsRepository.save(sms);
    }

    public void BulkSMSNotification(String message, String phoneNumber,String bulkCode,String smsTemplate) {
        SmsReqDto reqDto = new SmsReqDto();

        reqDto.setPhoneNumber(phoneNumber);
        reqDto.setMessage(message);
        reqDto.setBulkCode(bulkCode);
        reqDto.setBulk(true);
        reqDto.setBulkTemplate(smsTemplate);
        smsServiceV2.SMSNotification(reqDto);
    }





}
