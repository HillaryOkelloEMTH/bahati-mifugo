package com.emtech.dairyapp.Configurations.ProductConfig;


import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductConfigService {

    @Autowired
    private ProductConfigRepo productConfigRepo;



    public EntityResponse addProductConfig(ProductConfig productConfig){
        log.info("Adding new ProductConfig ...");
        EntityResponse response = new EntityResponse();
        try{
            productConfig.setCreatedDate(new Date());
            productConfigRepo.save(productConfig);
            log.info("Saving ProductConfig ...");
            response.setEntity(productConfig);
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
            return response;


        }catch (Exception e){
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse fetchProductConfig() {
        log.info("Fetching ProductConfigs ...");
        EntityResponse response = new EntityResponse();
        try {
            List<ProductConfig> ProductConfigs = productConfigRepo.findAll();
            if(ProductConfigs.size()>0) {
                log.info("ProductConfigs Found "+ "("+ProductConfigs.size()+")");
                response.setEntity(ProductConfigs);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                log.info("ProductConfigs Not Found "+ "("+ProductConfigs.size()+")");
                response.setEntity(ProductConfigs);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse updateProductConfig(ProductConfig productConfig) {
        EntityResponse response = new EntityResponse();
        try {
            productConfig.setModifiedDate(new Date());
            ProductConfig p= productConfigRepo.save(productConfig);
            response.setEntity(p);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            return response;


        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse deleteProductConfig(Long id) {
        EntityResponse response = new EntityResponse();
        try {
            Optional<ProductConfig> ProductConfig = productConfigRepo.findById(id);
            if(ProductConfig.isPresent()){
                productConfigRepo.deleteById(id);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("ProductConfig deleted Successfully");
                return response;

            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("ProductConfig with id "+id+"Not Found");
                return response;

            }
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
}
