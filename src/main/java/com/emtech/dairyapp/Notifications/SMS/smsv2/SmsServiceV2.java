package com.emtech.dairyapp.Notifications.SMS.smsv2;

import com.emtech.dairyapp.Notifications.SMS.smsv1.SMSNOtificaionRepo;
import com.emtech.dairyapp.Notifications.SMS.smsv1.SMSNotifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class SmsServiceV2 {


    @Value("${tilil.api_key}")
    private String apiKey;

    @Value("${tilil.sendsms.url}")
    private String sendSmsUrl;
    @Value("${tilil.userId}")
    private String sendSmsUserID;


    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private SMSNOtificaionRepo smsNotificationsRepository;


    public Mono<List<SMSResponse>> sendSMSNotification(String message, String mobile){

        SMSRequest smsRequest = new SMSRequest();
        smsRequest.setApi_key(apiKey);
        smsRequest.setMessage(message);
        smsRequest.setMobile(mobile);
        smsRequest.setShortcode(sendSmsUserID);
        smsRequest.setService_id(0);
        smsRequest.setResponse_type("json");

    return webClientBuilder.build()
            .post()
            .uri(sendSmsUrl)
            .body(Mono.just(smsRequest), SMSRequest.class)
            .exchange()
            .flatMap(clientResponse -> {
               if (clientResponse.statusCode().is2xxSuccessful()){
                   return clientResponse.bodyToFlux(SMSResponse.class)
                           .collectList()
                           .doOnSuccess(body-> {
                               Double bal = body.get(0).getCredit_balance();
                               if (bal == 50.0 || bal == 100.0 || bal == 200.0 || bal == 1000.0 || bal == 500.0 || bal == 2000.0){
//                                   SMSNotification("The credit amount balance is at "+ bal+" units", "254708145423");
                                   SMSNotification("The credit amount balance is at "+ bal+" units", "254112209296");

                                   SMSNotification("Hello Njogu The credit amount balance is at "+ bal+" units.", "254707454022");

                                   SMSNotification("The credit amt balance for Jufred is"+ bal+" units.", "254722585903");


                               }
                               log.info("The response is ::: {} and body is {}", clientResponse.statusCode(), body);
                           });
               }else {
                   return clientResponse.bodyToFlux(SMSResponse.class)
                           .collectList()
                           .flatMap(body -> {
                               log.error("Failed to send SMS with status code: {}, Response Body: {}", clientResponse.statusCode(), body);
                               return Mono.error(new RuntimeException("Failed to send SMS"));
                           });
               }
            });
    }


    public void SMSNotification(String message, String phoneNumber) {
        //Create Message and Save In DB
        Mono<List<SMSResponse>> sr = sendSMSNotification(message, phoneNumber);
        sr.subscribe(smsResponses -> {
            if (!smsResponses.isEmpty()){
                SMSResponse response = smsResponses.get(0);

                SMSNotifications sms = new SMSNotifications();
                sms.setResponseCode(Integer.valueOf(response.getStatus_code()));
                sms.setEventType("-");
                sms.setDeliveryTime("-");
                sms.setMessageRef(generatecSystemCode(10));
                sms.setMessageId(String.valueOf(response.getMessage_id()));
                sms.setMessage(message);
                sms.setSentDate(new Date());
                sms.setPhoneNumber(phoneNumber);
                smsNotificationsRepository.save(sms);
            }
        });
    }
    public static String generatecSystemCode(int len) {
        String chars = "JUFREDDAIRYFARM1234567890";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < 12; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length()))).toString();
        log.info("RANDOM STRING :: " + sb);
        return sb.toString();
    }



}
