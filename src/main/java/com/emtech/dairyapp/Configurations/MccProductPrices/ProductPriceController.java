package com.emtech.dairyapp.Configurations.MccProductPrices;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/product-prices")
public class ProductPriceController {
    @Autowired
    private final ProductPriceService priceService;

    @PostMapping("create/{productId}/{locationId}")
    public ResponseEntity<?> createProductPrice(@PathVariable Long productId, @PathVariable Long locationId, @RequestParam Double sellingPrice, @RequestParam Date effectiveFrom) {
        var response = priceService.createProductPrice(productId, locationId, sellingPrice,effectiveFrom);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("all")
    public ResponseEntity<?> getAllProductPrices() {
        var response = priceService.getAllProductPrices();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PutMapping("update/{productId}/{locationId}/{sellingPrice}")
    public ResponseEntity<?> updateProductPrice(@PathVariable Long productId, @PathVariable Long locationId, @PathVariable Double sellingPrice) {
        var response = priceService.updateProductPrice(productId, locationId, sellingPrice);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
