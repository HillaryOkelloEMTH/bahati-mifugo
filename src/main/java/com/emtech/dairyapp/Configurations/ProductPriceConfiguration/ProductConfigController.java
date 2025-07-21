package com.emtech.dairyapp.Configurations.ProductPriceConfiguration;

import com.emtech.dairyapp.Response.EntityResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("api/v1/product/configuration")
public class ProductConfigController {
    private  final ProductConfigService service;
    public ProductConfigController(ProductConfigService service) {
        this.service = service;
    }


    @PostMapping("add")
    public ResponseEntity<?> addProductConfig(@RequestBody ProductConfig productConfig){
        log.info("Receiving request...");
        var response = service.addProductConfig(productConfig);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("add/center")
    public ResponseEntity<?> addCenterConfig(@RequestBody ProductConfig productConfig) {
        var res = service.addCenterConfig(productConfig);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @GetMapping("get")
    public ResponseEntity<?> getProductConfigs(){
        var response = service.fetchProductConfig();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("get/config/centers")
    public ResponseEntity<?> getAllCenterConfigs() {
        var res = service.fetchAllCenterConfigs();
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @GetMapping("get/config")
    public ResponseEntity<?> getCenterConfigs(@RequestParam String centerName) {
        var res = service.fetchCenterConfigs(centerName);
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @GetMapping("id")
    public ResponseEntity<EntityResponse> getproductConfigById(@RequestParam Long productconfigId){
        EntityResponse response = service.fetchProductConfigById(productconfigId);
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("update")
    public ResponseEntity<?> updateProductConfig(@RequestBody ProductConfig productConfig, @NotBlank @RequestParam String filterType){
        var response = service.updateProductConfig(productConfig, filterType);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<EntityResponse> deleteproductConfig(@PathVariable Long id){
        EntityResponse response = service.deleteProductConfig(id);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("price/change/history")
    public ResponseEntity<EntityResponse> getproductPriceChangeHistory(@RequestParam Long productConfigId){
        EntityResponse response = service.fetchProductChangeHistory(productConfigId);
        return ResponseEntity.ok().body(response);
    }
}
