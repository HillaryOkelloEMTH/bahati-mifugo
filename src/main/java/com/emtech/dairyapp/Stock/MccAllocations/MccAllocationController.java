package com.emtech.dairyapp.Stock.MccAllocations;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/mcc-allocations")
@RequiredArgsConstructor
public class MccAllocationController {
    @Autowired
    private MccAllocationService mccAllocationService;

    @GetMapping("allocate/{productId}/{locationId}/{stock}")
    public ResponseEntity<?> allocateProducts(@PathVariable Long productId,@PathVariable Long locationId,@PathVariable Integer stock) {
        var response = mccAllocationService.allocateProducts(productId, locationId, stock);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("get/{locationId}/{graderId}")
    public ResponseEntity<?> getMccProducts(@PathVariable Long locationId, @PathVariable Long graderId) {
        var response = mccAllocationService.getMccProducts(locationId, graderId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
