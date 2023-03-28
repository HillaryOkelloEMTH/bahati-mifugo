package com.emtech.dairyapp.Transactions.Mpesa;

import com.emtech.dairyapp.Transactions.Data.Http.Response.B2CResponse;
import com.emtech.dairyapp.Transactions.Data.Http.Response.STKPushResponse;
import com.emtech.dairyapp.Transactions.Payment.Payment;
import com.emtech.dairyapp.Transactions.Payment.PaymentRepository;
import com.google.gson.Gson;
import lombok.NonNull;
import lombok.extern.java.Log;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Service
@Log
public class MpesaService {
    @Autowired
    private MpesaTransactionRepository mpesaTransactionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${mpesa.app.key}")
    private String appKeY;

    @Value("${mpesa.app.secret}")
    private String appSecret;

    @Value("${mpesa.token.url}")
    private String stkAuthUrl;

    @Value("${mpesa.stk.url}")
    private String stkUrl;

    @Value("${mpesa.stk.transactionType}")
    private String transactionType;

    @Value("${mpesa.stk.password}")
    private String password;

    @Value("${mpesa.stk.shortCode}")
    private String shortCode;

    @Value("${mpesa.stk.callbackURL}")
    private String callBackUrl;

    @Value("${mpesa.b2c.url}")
    private  String b2cUrl;

    @Value("${mpesa.b2c.securityCredential}")
    private String securityCredential;
    @Value("${mpesa.b2c.initiatorPassword}")
    private String initiatorPassword;

    @Value("${mpesa.b2c.commandId}")
    private String b2cCommandId;

    @Value("${mpesa.b2c.queTimeOutURL}")
    private String queTimeOutURL;

    @Value("${mpesa.b2c.callBackURL}")
    private String b2cResultUrl;

    @Value("${mpesa.b2c.shortCode}")
    private  String b2cShortCode;

    @Value("${mpesa.b2c.initiatorName}")
    private String initiatorName;

    Gson gson = new Gson();

    public String generateToken() throws IOException {
        String appKeySecret = appKeY + ":" + appSecret;
        byte[] bytes = appKeySecret.getBytes(StandardCharsets.ISO_8859_1);
        String encoded = Base64.getEncoder().encodeToString(bytes);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(stkAuthUrl)
                .get()
                .addHeader("authorization", "Basic "+encoded)
                .addHeader("cache-control", "no-cache")
                .build();
        Response response = client.newCall(request).execute();
        JSONObject jsonObject=new JSONObject(response.body().string());
        log.info("Mpesa Service Generate Token { "+jsonObject.getString("access_token")+" }");
        return jsonObject.getString("access_token");
    }

    public STKPushResponse initiateSTKPush(@NonNull Double amount, @NonNull String phoneNumber){
        STKPushResponse stk  = new STKPushResponse();
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(300, TimeUnit.SECONDS).build();
            MediaType mediaType = MediaType.parse("application/json");

            log.log(Level.INFO, String.format("Short code: %s", shortCode));

            JSONObject jo = new JSONObject();
            JSONArray ja = new JSONArray();
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
            jo.put("BusinessShortCode", shortCode);
            jo.put("Password", generatePassword(shortCode, password, timestamp));
            jo.put("Timestamp", timestamp);
            jo.put("Amount", amount);
            jo.put("TransactionType", transactionType);
            jo.put("PartyA", phoneNumber);
            jo.put("PartyB", shortCode);
            jo.put("PhoneNumber", phoneNumber);
            jo.put("CallBackURL", callBackUrl);
            jo.put("AccountReference", "Bahati");
            jo.put("TransactionDesc", "DEPOSIT TO BAHATI");

            log.log(Level.INFO, String.format("Initiate STK Push Request Body : %s ", jo));

            String requestJson = ja.put(jo).toString().replaceAll("[\\[\\]]", "");
            RequestBody body = RequestBody.create(mediaType, requestJson);
            String token = generateToken();
            System.out.println("Token - " + String.format("Bearer" + " " + "%s", token));

            Request request = new Request.Builder()
                    .url(stkUrl)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", String.format("Bearer" + " " + "%s", token))
                    .build();

            Response response = client.newCall(request).execute();
            log.info("{ Mpesa Service } {Initiate STK Push } { Successful }");
            stk = gson.fromJson(response.body().string(), STKPushResponse.class);

            AtomicReference<MpesaSTKTransaction> transaction = new AtomicReference<>(new MpesaSTKTransaction());
            transaction.get().setResponseCode(stk.getResponseCode());
            transaction.get().setMerchantRequestID(stk.getMerchantRequestID());
            transaction.get().setCheckoutRequestID(stk.getCheckoutRequestID());
            transaction.get().setCustomerMessage(stk.getCustomerMessage());
            transaction.get().setResponseDescription(stk.getResponseDescription());
            transaction.get().setPhoneNumber(phoneNumber);
            transaction.get().setAmount(amount);

            transaction.set(this.mpesaTransactionRepository.save(transaction.get()));

            AtomicReference<Payment> payment = new AtomicReference<>(new Payment());
            payment.get().setMerchantRequestID(stk.getMerchantRequestID());
            payment.get().setTransactionType("STK PUSH");
            payment.get().setStatus("Pending");

            payment.set(this.paymentRepository.save(payment.get()));
        }
        catch (Exception e)
        {
            log.info("{ Mpesa Service } {Initiate STK Push } { ERROR } - "+e.getMessage());
            stk.setCheckoutRequestID(null);
            stk.setCustomerMessage("ERROR");
            stk.setCheckoutRequestID(null);
            stk.setMerchantRequestID(null);
            stk.setResponseDescription(e.getLocalizedMessage());
        }

        return stk;
    }

    public String generatePassword(String shortCode,String passkey,String timeStamp)
    {
        String credentials = shortCode+passkey+timeStamp;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public B2CResponse initiateB2CRequest(@NonNull Double amount, @NonNull String phoneNumber){
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(1000, TimeUnit.SECONDS).readTimeout(3000, TimeUnit.SECONDS).build();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject oj = new JSONObject();
        JSONArray aj = new JSONArray();
        B2CResponse b2c = new B2CResponse();

        //String securityCredential = getSecurityCredentials(initiatorPassword);
        oj.put("InitiatorName", initiatorName);
        oj.put("SecurityCredential", securityCredential);
        oj.put("CommandID", b2cCommandId);
        oj.put("Amount", amount);
        oj.put("PartyA", b2cShortCode);
        oj.put("PartyB", phoneNumber);
        oj.put("Remarks", "BAHATI DIARIES TO M-PESA NO. "+phoneNumber);
        oj.put("QueueTimeOutURL", queTimeOutURL);
        oj.put("ResultURL", b2cResultUrl);
        oj.put("Occassion", "Bahati Diaries");

        log.log(Level.INFO, String.format("B2C Request %s ", oj));

        try {
            String requestJson = aj.put(oj).toString().replaceAll("[\\[\\]]", "");
            RequestBody body = RequestBody.create(mediaType, requestJson);

            String token = generateToken();

            Request request = new Request.Builder()
                    .url(b2cUrl)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", String.format("Bearer" + " " + "%s", token))
                    .build();

            Response response = client.newCall(request).execute();
            assert response.body() != null;
            String res = response.body().string();
            log.info("{ Mpesa Service } { Initiate B2C }");
            b2c = gson.fromJson(res, B2CResponse.class);
        }
        catch (Exception e)
        {
            log.info("{ Mpesa Service } {Initiate B2C } { ERROR } - "+e.getMessage());
            b2c.setConversationID("ERROR");
            b2c.setResponseDescription(e.getLocalizedMessage());
            b2c.setOriginatorConversationID("-");
            b2c.setResponseCode("-");
        }
        return b2c;
    }

    public void processSTKPushCallBack(@NonNull Object object){
        Gson gson = new Gson();
        log.log(Level.INFO, "STK Callback received at " + new Date());
        JSONObject j1 = new JSONObject(gson.toJson(object));

        log.log(Level.INFO, String.format("STK Init Callback Response %s", j1));

        String resultCode;
        String resultDesc;
        String mpesaCode = "";
        String merchantRequestId;
        Date transactionDate = null;
        long phoneNumber = 0;
        Double amount = null;

        if (j1.has("Body")) {
            JSONObject j2 = j1.getJSONObject("Body");
            if (j2.has("stkCallback")) {
                JSONObject j3 = j2.getJSONObject("stkCallback");
                if(j3.has("ResultCode"))
                {
                    resultCode = String.valueOf(j3.getInt("ResultCode"));
                } else {
                    resultCode = "";
                }
                if(j3.has("ResultDesc"))
                {
                    resultDesc = j3.getString("ResultDesc");
                } else {
                    resultDesc = "";
                }
                if(j3.has("MerchantRequestID"))
                {
                    merchantRequestId = j3.getString("MerchantRequestID");
                } else {
                    merchantRequestId = "";
                }

                if (j3.has("CallbackMetadata")) {

                    JSONObject j4 = j3.getJSONObject("CallbackMetadata");
                    log.log(Level.INFO, String.format("Callback Metadata: %s ", j4));
                    if (j4.has("Item")) {
                        JSONArray ja = j4.getJSONArray("Item");
                        for (Object ob : ja) {
                            JSONObject j5 = new JSONObject(ob.toString());
                            if (j5.getString("Name").equalsIgnoreCase("MpesaReceiptNumber")) {
                                mpesaCode = j5.getString("Value");

                                log.log(Level.INFO, String.format("Mpesa Reference Number: %s ", mpesaCode));
                            }

                            if (j5.getString("Name").equalsIgnoreCase("Amount")) {
                                amount = j5.getDouble("Value");

                                log.log(Level.INFO, String.format("Amount: %s", amount));
                            }

                            if (j5.getString("Name").equalsIgnoreCase("TransactionDate")) {
                                long myTransactionDate = j5.getLong("Value");

                                transactionDate = new Date(myTransactionDate);

                                log.log(Level.INFO, String.format("Transaction Date: %s ", transactionDate));
                            }

                            if (j5.getString("Name").equalsIgnoreCase("PhoneNumber")) {
                                phoneNumber = j5.getLong("Value");

                                log.log(Level.INFO, String.format("Phone Number : %s ", phoneNumber));
                            }
                        }

                    }
                }
            } else {
                merchantRequestId = "";
                resultDesc = "";
                resultCode = "";
            }
        } else {
            merchantRequestId = "";
            resultDesc = "";
            resultCode = "";
        }

        Double finalAmount = amount;
        String finalMpesaCode = mpesaCode;
        Date finalTransactionDate = transactionDate;
        long finalPhoneNumber = phoneNumber;
        this.paymentRepository.findByMerchantRequestID(merchantRequestId).ifPresentOrElse(payment -> {

            AtomicReference<Payment> myPayment = new AtomicReference<>(payment);

            if(Integer.parseInt(resultCode) == 0){

                myPayment.get().setAmount(finalAmount);
                myPayment.get().setResultCode(resultCode);
                myPayment.get().setMpesaReceiptNumber(finalMpesaCode);
                myPayment.get().setResultDescription(resultDesc);
                assert finalTransactionDate != null;
                myPayment.get().setTransactionDate(new Timestamp(finalTransactionDate.getTime()));
                myPayment.get().setPhoneNumber(finalPhoneNumber);
                myPayment.get().setStatus("Success");

                myPayment.set(this.paymentRepository.save(myPayment.get()));
            }else {
                myPayment.get().setResultCode(resultCode);
                myPayment.get().setMpesaReceiptNumber(finalMpesaCode);
                myPayment.get().setResultDescription(resultDesc);
                myPayment.get().setStatus("Failed");

                myPayment.set(this.paymentRepository.save(myPayment.get()));
            }


        },() -> {
            log.log(Level.INFO, String.format("transaction with the request merchant id %s not found ", merchantRequestId));
        });

    }


}
