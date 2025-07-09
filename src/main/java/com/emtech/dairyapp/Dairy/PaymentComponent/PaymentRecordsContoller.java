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
    public ResponseEntity<EntityResponse> getFilterPaymentData(@RequestParam String month,@RequestParam String paymentMode, @RequestParam Character paymentStatus){
        EntityResponse response = paymentRecordsService.getFilteredFarmerPaymentData(month, paymentMode,paymentStatus);
        return ResponseEntity.ok().body(response);

    }
    @GetMapping("filter/location/{locationId}")
    public ResponseEntity<EntityResponse> getFilterPaymentDataByLocation(
            @PathVariable Long locationId){

        EntityResponse response = paymentRecordsService.getFilterFarmerPaymentDataByLocation(locationId);

        return ResponseEntity.ok().body(response);
    }
    @GetMapping("filter/farmer/{farmerNo}")
    public ResponseEntity<EntityResponse> getFilterPaymentDataByFarmer(
            @PathVariable String farmerNo
    ){

        EntityResponse response = paymentRecordsService.getFilterFarmerPaymentDataByFarmerNo(farmerNo);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("filter/date/{from}/{to}")
    public ResponseEntity<EntityResponse> getFilterPaymentDataByDateRange(
            @PathVariable String from,
            @PathVariable String to
    ){

        EntityResponse response = paymentRecordsService.getFilterPaymentDataByDateRange(from, to);

        return ResponseEntity.ok().body(response);
    }

}
