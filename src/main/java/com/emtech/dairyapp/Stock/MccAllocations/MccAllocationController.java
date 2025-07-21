package com.emtech.dairyapp.Stock.MccAllocations;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
@RestController
@RequestMapping("api/v1/mcc-allocations")
@RequiredArgsConstructor
public class MccAllocationController {
    @Autowired
    private MccAllocationService mccAllocationService;

    @PostMapping("allocate/{productId}/{locationId}/{stock}")
    public ResponseEntity<?> allocateProducts(@PathVariable Long productId,@PathVariable Long locationId,@PathVariable Integer stock) {
        var response = mccAllocationService.allocateProducts(productId, locationId, stock);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("transfer/{sourceId}/{destinationId}/{productId}/{stock}")
    public ResponseEntity<?> transferProducts(@PathVariable Long sourceId,@PathVariable Long destinationId,@PathVariable Long productId, @PathVariable Integer stock) {
        var response = mccAllocationService.stockTransfer(sourceId, destinationId, productId, stock);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("get/{locationId}")
    public ResponseEntity<?> getMccProducts(@PathVariable Long locationId) {
        var response = mccAllocationService.getMccProducts(locationId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("get/filter")
    public ResponseEntity<?> getFilterMccProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
            ){

//        if (month < 1 || month > 12){
//
//        }
        var response = mccAllocationService.getFilterMccProducts(locationId, productId, startDate, endDate);

        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @GetMapping("all")
    ResponseEntity<?> getAllMccAllocations() {
        var response = mccAllocationService.getAllMccProducts();

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
