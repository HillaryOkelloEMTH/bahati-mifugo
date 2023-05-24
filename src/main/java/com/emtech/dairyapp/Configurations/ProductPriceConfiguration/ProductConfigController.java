package com.emtech.dairyapp.Configurations.ProductPriceConfiguration;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<EntityResponse> addproductConfig(@RequestBody ProductConfig productConfig){
        log.info("Receiving request...");
        EntityResponse response = service.addProductConfig(productConfig);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("get")
    public ResponseEntity<EntityResponse> getproductConfigs(){
        EntityResponse response = service.fetchProductConfig();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("id")
    public ResponseEntity<EntityResponse> getproductConfigById(@RequestParam Long productconfigId){
        EntityResponse response = service.fetchProductConfigById(productconfigId);
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("update")
    public ResponseEntity<EntityResponse> updateproductConfig(@RequestBody ProductConfig productConfig){
        EntityResponse response = service.updateProductConfig(productConfig);
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
