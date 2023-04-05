package com.emtech.dairyapp.Dairy.PaymentComponent;


import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/payments")
public class PaymentRecordsContoller {


    @Autowired
    private PaymentRecordsService paymentRecordsService;


    @GetMapping("records")
    public ResponseEntity<EntityResponse> getPaymentData(){
        EntityResponse response = paymentRecordsService.getFarmerPaymentData();
        return ResponseEntity.ok().body(response);

    }
    @GetMapping("filter")
    public ResponseEntity<EntityResponse> getFilterPaymentData(@RequestParam String month,@RequestParam String paymentMode){
        EntityResponse response = paymentRecordsService.getFilteredFarmerPaymentData(month, paymentMode);
        return ResponseEntity.ok().body(response);

    }
}
