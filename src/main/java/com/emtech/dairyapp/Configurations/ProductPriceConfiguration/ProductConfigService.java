package com.emtech.dairyapp.Configurations.ProductPriceConfiguration;


import com.emtech.dairyapp.Configurations.Routes.Route;
import com.emtech.dairyapp.Configurations.Routes.RouteRepo;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductConfigService {

    @Autowired
    private ProductConfigRepo productConfigRepo;
    @Autowired
    private PriceChangeHistoryRepo priceChangeHistoryRepo;
    @Autowired
    private RouteRepo routeRepo;


    public EntityResponse addProductConfig(ProductConfig productConfig){
        log.info("Adding new ProductConfig ...");
        EntityResponse response = new EntityResponse();
        try{
            Optional<ProductConfig> duplicatecheck= productConfigRepo.findByRouteFk(productConfig.getRouteFk());
            if(duplicatecheck.isPresent()){
                Optional<Route> r = routeRepo.findById(productConfig.getRouteFk());
                response.setEntity(duplicatecheck.get());
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Price Configuration for "+ r.get().getRoute() + " already exist!");
                log.info("Price Configuration for "+ r.get().getRoute() + " already exist!");
                return response;
            }
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
            List<ProductConfigRepo.AllProductConfig> ProductConfigs = productConfigRepo.getAllProducConfig();
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
    public EntityResponse fetchProductConfigById(Long productConfId) {
        log.info("Fetching ProductConfig with id "+productConfId  +"...");
        EntityResponse response = new EntityResponse();
        try {
            Optional<ProductConfig> ProductConfigs = productConfigRepo.findById(productConfId);
            if(ProductConfigs.isPresent()) {
                log.info("ProductConfigs Found ");
                response.setEntity(ProductConfigs);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                log.info("ProductConfigs Not Found ");
                response.setEntity(ProductConfigs);
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse fetchProductChangeHistory(Long productId) {
        log.info("Fetching Product Price change history ...");
        EntityResponse response = new EntityResponse();
        try {
            List<PriceChangeHistory> priceChanges = priceChangeHistoryRepo.findByProductConfigId(productId);
            if(priceChanges.size()>0) {
                log.info("Product price changes Found "+ "("+priceChanges.size()+")");
                response.setEntity(priceChanges);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                log.info("Product Price changes Not Found ");
                response.setEntity(priceChanges);
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
            Optional<ProductConfig> pc= productConfigRepo.findById(productConfig.getId());
            if(pc.isPresent()) {
                ProductConfig p= pc.get();
                PriceChangeHistory ch = new PriceChangeHistory();

                Long productId = productConfig.getId();
                ch.setOldPrice(p.getBuyingPrice());
                ch.setProductConfigId(productId);
                ch.setNewPrice(productConfig.getBuyingPrice());
//                ch.setModifiedBy(auth.getName());
                ch.setModifiedDate(new Date());
                ch.setProductName(p.getProductName());
                ch.setRouteFk(productConfig.getRouteFk());
                priceChangeHistoryRepo.save(ch);

                productConfig.setModifiedDate(new Date());
                ProductConfig newp = productConfigRepo.save(productConfig);
                response.setEntity(newp);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.OK.getReasonPhrase());

            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }
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
