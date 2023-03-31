package com.emtech.dairyapp.Transactions.Payment;

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
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Service
@Log
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

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
                        .mpesaReceiptNumber(payment.getMpesaReceiptNumber())
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
                        .mpesaReceiptNumber(payment.getMpesaReceiptNumber())
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
                    .mpesaReceiptNumber(payment.getMpesaReceiptNumber())
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
}
