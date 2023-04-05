package com.emtech.dairyapp.Transactions.Payment;

import com.emtech.dairyapp.Transactions.Data.Http.Request.CashPaymentRequest;
import com.emtech.dairyapp.Transactions.Data.Http.Response.PaymentEntityResponse;
import com.emtech.dairyapp.Transactions.Data.Http.Response.PaymentResponse;
import com.emtech.dairyapp.Transactions.Data.Http.Response.PaymentsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(
        path = "/api/v1/payments"
)
public class PaymentController {
    @Autowired
    private PaymentService paymentService;


    @RequestMapping(
            path = "/cash-payment",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<PaymentEntityResponse>> processCashPayment(@RequestBody CashPaymentRequest body){
        return Mono.just(ResponseEntity.ok().body(this.paymentService.processCashPayment(body.getAmount(), body.getMobile(), body.getCollectorId(), body.getCollectionId())));
    }

    @RequestMapping(
            path = "/all-payments",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<PaymentsResponse>> findAllPayments(){
        return Mono.just(ResponseEntity.ok().body(this.paymentService.findAllPayments()));
    }

    @RequestMapping(
            path = "/pending-payments",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<PaymentsResponse>> findAllPendingPayments(){
        return Mono.just(ResponseEntity.ok().body(this.paymentService.findAllPaymentsByStatus("Pending")));
    }

    @RequestMapping(
            path = "/successful-payments",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<PaymentsResponse>> findAllSuccessfulPayments(){
        return Mono.just(ResponseEntity.ok().body(this.paymentService.findAllPaymentsByStatus("Success")));
    }

    @RequestMapping(
            path = "/failed-payments",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<PaymentsResponse>> findAllFailedPayments(){
        return Mono.just(ResponseEntity.ok().body(this.paymentService.findAllPaymentsByStatus("Failed")));
    }

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<PaymentResponse>> findPaymentDetails(@PathVariable Long id){
        return Mono.just(ResponseEntity.ok().body(this.paymentService.findPaymentDetails(id)));
    }
}
