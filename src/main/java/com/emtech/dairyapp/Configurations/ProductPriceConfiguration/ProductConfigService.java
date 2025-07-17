package com.emtech.dairyapp.Configurations.ProductPriceConfiguration;


import com.emtech.dairyapp.Auth.Utilities.UserInfo;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocationsRepo;
import com.emtech.dairyapp.Configurations.Routes.Route;
import com.emtech.dairyapp.Configurations.Routes.RouteRepo;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private PickUpLocationsRepo pickUpLocationsRepo;
    @Autowired private UserInfo userInfo;


    @Transactional
    public EntityResponse<?> addCenterConfig(ProductConfig productConfig){
        log.info("Creating new center product config ...");
        EntityResponse<String> response = new EntityResponse<>();
        try{
            productConfigRepo.findByMcc(productConfig.getMccFk()).ifPresentOrElse(
                    (p) -> {
                        response.setMessage("Config already exists");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        log.info("Price Configuration for Center already exist!");
                    },
                    () -> {
                        pickUpLocationsRepo.findById(productConfig.getMccFk()).ifPresentOrElse(
                                (pul) -> {
                                    log.info("Setting up price config for {} on {}", pul.getName(), LocalDateTime.now());
                                    productConfig.setCreatedDate(new Date());
                                    productConfig.setCreatedBy(UserInfo.username());
                                    productConfigRepo.save(productConfig);

                                    log.info("Setting up price configs for all routes in {}", pul.getName());

                                    List<ProductConfig> routeConfigs = new ArrayList<>();
                                    routeRepo.findCenterRoutes(pul.getId()).forEach(
                                            (r) -> {
                                                ProductConfig pRouteConfig = new ProductConfig();
                                                pRouteConfig.setProductName(productConfig.getProductName());
                                                pRouteConfig.setBuyingPrice(productConfig.getBuyingPrice());
                                                pRouteConfig.setSellingPrice(productConfig.getBuyingPrice());
                                                pRouteConfig.setUnitMeasurement(productConfig.getUnitMeasurement());
                                                pRouteConfig.setQuantity(productConfig.getQuantity());
                                                pRouteConfig.setEffectiveFrom(productConfig.getEffectiveFrom());
                                                pRouteConfig.setCreatedDate(new Date());
                                                pRouteConfig.setMccFk(null);
                                                pRouteConfig.setRouteFk(r.getId());
                                                productConfig.setCreatedBy(UserInfo.username());
                                                routeConfigs.add(pRouteConfig);
                                            }
                                    );

                                    productConfigRepo.saveAll(routeConfigs);
                                    response.setStatusCode(HttpStatus.CREATED.value());
                                    response.setMessage("Price config for "+pul.getName()+" and its routes created");
                                },
                                () -> {
                                    response.setMessage("Center with id "+productConfig.getMccFk()+" not found");
                                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                                }
                        );
                    }
            );

        }catch (Exception e){
            log.error("A server error was caught: {}", e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse addProductConfig(ProductConfig productConfig){
        log.info("Adding new ProductConfig ...");
        EntityResponse response = new EntityResponse();
        try{
            Optional<ProductConfig> duplicatecheck= productConfigRepo.findByMccFkAndRouteFk(productConfig.getRouteFk(), productConfig.getMccFk());
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

    public EntityResponse<?> fetchAllCenterConfigs() {
        log.info("Fetching ProductConfig for centers");
        EntityResponse<List<ProductConfigRepo.AllProductConfig>> response = new EntityResponse<>();
        try {
            List<ProductConfigRepo.AllProductConfig> configs = productConfigRepo.findAllMccConfigs();

            response.setEntity(configs);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage(HttpStatus.FOUND.getReasonPhrase());
        } catch (Exception e) {
            log.error("Error: {}", e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse<?> fetchCenterConfigs(String center) {
        log.info("Fetching ProductConfig for center {}", center);
        EntityResponse<List<ProductConfigRepo.AllProductConfig>> response = new EntityResponse<>();
        try {
            pickUpLocationsRepo.findByName(center).ifPresentOrElse(
                    (p) -> {
                        List<ProductConfigRepo.AllProductConfig> configs = productConfigRepo.findCenterConfigs(p.getId());

                        System.out.println("the size of the data is "+configs.size());
                        System.out.println("the first record is "+configs.get(0));

                        response.setEntity(configs);
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    },
                    () -> {
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setMessage("Center with name "+center+" not found");
                    }
            );
        } catch (Exception e) {
            log.error("A server error occurred {}", e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
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

    @Transactional
    public EntityResponse<?> updateProductConfig(ProductConfig productConfig, String updateType) {
        EntityResponse<ProductConfig> response = new EntityResponse<>();
        try {
            productConfigRepo.findById(productConfig.getId()).ifPresentOrElse(
                    (p) -> {
                        log.info("Saving the price history change and update");
                        PriceChangeHistory ch = new PriceChangeHistory();

                        if (updateType.equalsIgnoreCase("route")) {
                            log.info("Updating buying price for route with id {}", p.getRouteFk());
                            p.setModifiedDate(new Date());
                            p.setUpdatedBy(UserInfo.username());
                            p.setBuyingPrice(productConfig.getBuyingPrice());
                            p.setStatus('Y');
                            p.setSellingPrice(productConfig.getSellingPrice());

                            saveHistory(p, productConfig);
                            productConfigRepo.save(p);
                            productConfigRepo.updateRouteCollectionPrices(p.getRouteFk(), productConfig.getBuyingPrice(), productConfig.getEffectiveFrom().toString());
                        } else {
                            log.info("Updating buying price for center with id {}", p.getMccFk());
                            p.setModifiedDate(new Date());
                            p.setUpdatedBy(UserInfo.username());
                            saveHistory(p, productConfig);
                            p.setBuyingPrice(productConfig.getBuyingPrice());
                            p.setStatus('Y');
                            p.setSellingPrice(productConfig.getSellingPrice());
                            productConfigRepo.save(p);

                            if (productConfig.getMccFk() != null && productConfig.getRouteFk() != null) {
                                productConfigRepo.findAllRouteConfigs(p.getMccFk()).forEach((pConfig) -> {
                                    log.info("Updating mcc price changes for route with id {}", p.getRouteFk());
                                    pConfig.setModifiedDate(new Date());
                                    pConfig.setUpdatedBy(UserInfo.username());
                                    pConfig.setBuyingPrice(productConfig.getBuyingPrice());
                                    pConfig.setStatus('Y');
                                    pConfig.setSellingPrice(productConfig.getSellingPrice());
                                    productConfigRepo.save(pConfig);
                                });
                            }

                            log.info("Updating total amount for collections based on new prices and effective dates.");
                            productConfigRepo.updateAllMccCollectionPrices(p.getMccFk(), productConfig.getBuyingPrice(), productConfig.getEffectiveFrom().toString());
                        }
                    },
                    () -> {
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setMessage("Price configuration not found");
                    }
            );
        } catch (Exception e) {
            log.error("Error caught when processing update {}", e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    private void saveHistory(ProductConfig p, ProductConfig productConfig) {
        PriceChangeHistory ch = new PriceChangeHistory();
        Long productId = productConfig.getId();
        ch.setOldPrice(p.getBuyingPrice());
        ch.setProductConfigId(productId);
        ch.setNewPrice(productConfig.getBuyingPrice());
        ch.setModifiedBy(UserInfo.username());
        ch.setModifiedDate(new Date());
        ch.setProductName(p.getProductName());
        ch.setRouteFk(productConfig.getRouteFk());
        ch.setMccFk(productConfig.getMccFk());
        priceChangeHistoryRepo.save(ch);
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
