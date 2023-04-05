package com.emtech.dairyapp.Transactions.Payment;

import com.emtech.dairyapp.Dairy.FloatTracking.FloatManager;
import com.emtech.dairyapp.Dairy.FloatTracking.FloatManagerRepo;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
import com.emtech.dairyapp.Dairy.Supply.MilkCollections;
import com.emtech.dairyapp.Transactions.Data.Http.Response.PaymentEntityResponse;
import com.emtech.dairyapp.Transactions.Data.Http.Response.PaymentResponse;
import com.emtech.dairyapp.Transactions.Data.Http.Response.PaymentsResponse;
import com.emtech.dairyapp.Transactions.Data.Payment.PaymentData;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Service
@Log
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FloatManagerRepo floatManagerRepo;

    @Autowired
    private MilkCollectionRepo milkCollectionRepo;

    public PaymentEntityResponse processCashPayment(@NonNull double amount,  @NonNull long collectorId, @NonNull long collectionId){
        AtomicReference<PaymentEntityResponse> response = new AtomicReference<>();

        this.milkCollectionRepo.findById(collectionId).ifPresentOrElse(collection -> {
            this.floatManagerRepo.findByCollectorId(collectorId).ifPresentOrElse(floatManager -> {
                if(floatManager.getBalance() < amount){
                    log.log(Level.WARNING, String.format("Amount to be paid %s is more than your float amount %s", amount, floatManager.getBalance()));

                    response.set(PaymentEntityResponse.builder().message("Amount to be paid is more than your float amount").statusCode(HttpStatus.BAD_REQUEST.value()).build());
                }else{
                    double newBalance = floatManager.getBalance() - amount;

                    AtomicReference<FloatManager> myFloatManager = new AtomicReference<>(floatManager);

                    myFloatManager.get().setBalance(newBalance);

                    myFloatManager.set(floatManagerRepo.save(myFloatManager.get()));

                    AtomicReference<Payment> payment = new AtomicReference<>(new Payment());
                    AtomicReference<MilkCollections> myCollection = new AtomicReference<>(collection);

                    payment.get().setAmount(amount);
                    payment.get().setStatus("Success");
                    payment.get().setTransactionType("Cash");
                    payment.get().setResultCode("0");
                    payment.get().setReceiptNumber(generatecSystemCode(10));
                    payment.get().setResultDescription("Successful cash payment");

                    payment.set(paymentRepository.save(payment.get()));
                    myCollection.get().setPaymentStatus('Y');

                    myCollection.set(this.milkCollectionRepo.save(myCollection.get()));

                    response.set(PaymentEntityResponse.builder().message(String.format("Payment with receipt number %s processed successfully", payment.get().getReceiptNumber())).statusCode(HttpStatus.OK.value()).build());
                }
            }, () -> {
                log.log(Level.WARNING, String.format("Collector with the id %s not found ", collectorId));
                response.set(PaymentEntityResponse.builder().message(String.format("Collector with the id %s not found ", collectorId)).statusCode(HttpStatus.BAD_REQUEST.value()).build());

            });
        }, () -> {
            log.log(Level.WARNING, (String.format("Collection with the is %s not found", collectionId)));
            response.set(PaymentEntityResponse.builder().message(String.format("Collection with the is %s not found", collectionId)).statusCode(HttpStatus.BAD_REQUEST.value()).build());
        });

        return response.get();
    }

    public PaymentsResponse findAllPayments(){
        AtomicReference<PaymentsResponse> response = new AtomicReference<>();

        List<PaymentData> paymentData = new ArrayList<>();

        List<Payment> payments = paymentRepository.findAll();

        if(!payments.isEmpty()){
            payments.forEach(payment -> {
                PaymentData myPayment = PaymentData.builder()
                        .id(payment.getId())
                        .resultCode(payment.getResultCode())
                        .merchantRequestID(payment.getMerchantRequestID())
                        .resultDescription(payment.getResultDescription())
                        .amount(payment.getAmount())
                        .mpesaReceiptNumber(payment.getReceiptNumber())
                        .transactionDate(payment.getTransactionDate())
                        .phoneNumber(payment.getPhoneNumber())
                        .status(payment.getStatus())
                        .createdDate(payment.getCreatedDate())
                        .build();

                paymentData.add(myPayment);

            });

            response.set(PaymentsResponse.builder().statusCode(HttpStatus.OK.value()).entity(paymentData).build());
        }else {
            log.log(Level.INFO, "No payments found");
        }

        return response.get();
    }

    public PaymentsResponse findAllPaymentsByStatus(@NonNull String status){
        AtomicReference<PaymentsResponse> response = new AtomicReference<>();

        List<PaymentData> paymentData = new ArrayList<>();

        List<Payment> payments = paymentRepository.findAllByStatus(status);

        if(!payments.isEmpty()){
            payments.forEach(payment -> {
                PaymentData myPayment = PaymentData.builder()
                        .id(payment.getId())
                        .resultCode(payment.getResultCode())
                        .merchantRequestID(payment.getMerchantRequestID())
                        .resultDescription(payment.getResultDescription())
                        .amount(payment.getAmount())
                        .mpesaReceiptNumber(payment.getReceiptNumber())
                        .transactionDate(payment.getTransactionDate())
                        .phoneNumber(payment.getPhoneNumber())
                        .status(payment.getStatus())
                        .createdDate(payment.getCreatedDate())
                        .build();

                paymentData.add(myPayment);

            });

            response.set(PaymentsResponse.builder().statusCode(HttpStatus.OK.value()).entity(paymentData).build());
        }else {
            log.log(Level.INFO, "No payments found");
        }

        return response.get();
    }

    public PaymentResponse findPaymentDetails(@NonNull Long id){
        AtomicReference<PaymentResponse> response = new AtomicReference<>();

        this.paymentRepository.findById(id).ifPresentOrElse(payment -> {
            PaymentData paymentData = PaymentData.builder()
                    .id(payment.getId())
                    .resultCode(payment.getResultCode())
                    .merchantRequestID(payment.getMerchantRequestID())
                    .resultDescription(payment.getResultDescription())
                    .amount(payment.getAmount())
                    .mpesaReceiptNumber(payment.getReceiptNumber())
                    .transactionDate(payment.getTransactionDate())
                    .phoneNumber(payment.getPhoneNumber())
                    .status(payment.getStatus())
                    .createdDate(payment.getCreatedDate())
                    .build();

            response.set(PaymentResponse.builder().statusCode(HttpStatus.OK.value()).entity(paymentData).build());
        }, () -> {
            log.log(Level.INFO, String.format("Payment with the id %s not found ", id));
        });

        return response.get();
    }

    public static String generatecSystemCode(int len) {
        String chars = "01234567890BAHATIDAIRIESPAYMENTS";
        Random rnd = new Random();
        String S = "S";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length()))).toString();
        return S + sb;
    }
}
