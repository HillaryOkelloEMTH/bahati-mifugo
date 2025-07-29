package com.emtech.dairyapp.Notifications.SMS.smsv2;

import com.emtech.dairyapp.Notifications.SMS.smsv1.SmsNotificationRepo;
import com.emtech.dairyapp.Notifications.SMS.smsv1.SMSNotifications;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    private SmsNotificationRepo smsNotificationsRepository;


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
                    System.out.println("The response from client is {}"+ clientResponse.toString());
                   if (clientResponse.statusCode().is2xxSuccessful()){
                       return clientResponse.bodyToFlux(SMSResponse.class)
                               .collectList()
                               .doOnSuccess(body-> {
                                   String phone = "";
                                   String msg = "";
                                   SmsReqDto reqDto = new SmsReqDto();
                                   reqDto.setBulk(false);
                                   reqDto.setPhoneNumber(phone);
                                   reqDto.setMessage(msg);
                                   Double bal = body.get(0).getCredit_balance();

                                   if (bal != null) {
                                       if (bal == 50.0 || bal == 100.0 || bal == 200.0 || bal == 1000.0 || bal == 500.0 || bal == 2000.0) {
    //                                   SMSNotification("The credit amount balance is at "+ bal+" units", "254708145423"); //
                                           reqDto.setMessage("The credit amount balance is at " + bal + " units");
                                           reqDto.setPhoneNumber("254112209296"); SMSNotification(reqDto);
                                           reqDto.setPhoneNumber("254722585903"); SMSNotification(reqDto);

                                           reqDto.setPhoneNumber("254719411709"); SMSNotification(reqDto);
                                           reqDto.setMessage("The credit amt balance for Bahati Dairies is" + bal + " units.");
                                           reqDto.setPhoneNumber("254715318204"); SMSNotification(reqDto);  // to bahati MD
                                       }
                                       log.info("The response is ::: {} and body is {}", clientResponse.statusCode(), body);
                                   }

                               });
                   }else {
                       System.out.println("Wereh ere tyring to debug.");
                       return clientResponse.bodyToFlux(SMSResponse.class)
                               .collectList()
                               .flatMap(body -> {
                                   log.error("Failed to send SMS with status code: {}, Response Body: {}", clientResponse.statusCode(), body);
                                   return Mono.error(new RuntimeException("Failed to send SMS"));
                               });
                   }
                });
    }

    public void SMSNotification(SmsReqDto dto) {
        //Create Message and Save In DB
        try {
            Mono<List<SMSResponse>> sr = sendSMSNotification(dto.getMessage(), dto.getPhoneNumber());

            sr.subscribe(smsResponses -> {
                if (!smsResponses.isEmpty()){
                    SMSResponse response = smsResponses.get(0);

                    SMSNotifications sms = new SMSNotifications();
                    sms.setResponseCode(Integer.parseInt(response.getStatus_code()));
                    sms.setEventType("-");
                    sms.setDeliveryTime("");
                    sms.setStatusDescription(response.getStatus_desc());
                    sms.setMessageRef(generatecSystemCode(10));
                    sms.setMessageId(String.valueOf(response.getMessage_id()));
                    sms.setMessage(dto.getMessage());
                    sms.setSentDate(new Date());
                    sms.setPhoneNumber(dto.getPhoneNumber());
                    sms.setNetworkId(response.getNetwork_id());

                    // saving bulk sms details
                    if (dto.isBulk()) {
                        sms.setBulk('Y');
                        sms.setCategory("Bulk");
                        sms.setBulkCode(dto.getBulkCode());
                        sms.setSmsTemplate(dto.getBulkTemplate());
                    }

                    smsNotificationsRepository.save(sms);
                }
            });
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public EntityResponse<?> getMessagesByDateRange(String from, String to) {
        EntityResponse<List<SMSNotifications>> res = new EntityResponse<>();

        try {
            if (from.isEmpty() || to.isEmpty()) {
                res.setMessage("From and to date required");
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
                return res;
            }

            List<SMSNotifications> notifications = smsNotificationsRepository.findByDateRange(from, to);

            res.setMessage("Found "+notifications.size()+" messages sent between "+from+" and to "+to);
            res.setStatusCode(HttpStatus.OK.value());
            res.setEntity(notifications);
        } catch (Exception e) {
            log.error(e.toString());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            res.setMessage("Failed to get records");
        }
        return res;
    }

    public static String generatecSystemCode(int len) {
        String chars = "BAHATIDAIRYFARM1234567890";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < 12; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        log.info("RANDOM STRING :: {}", sb);
        return sb.toString();
    }
}
