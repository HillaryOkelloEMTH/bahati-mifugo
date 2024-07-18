package com.emtech.dairyapp.Configurations.MccProductPrices;

import com.emtech.dairyapp.Configurations.Interfaces.PickUpLocation;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocations;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocationsRepo;
import com.emtech.dairyapp.Response.EntityResponse;
import com.emtech.dairyapp.Stock.Product.Product;
import com.emtech.dairyapp.Stock.Product.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductPriceService {
    @Autowired
    private ProductPriceRepository productPriceRepo;

    @Autowired
    private PickUpLocationsRepo pickUpLocationsRepo;

    @Autowired
    private ProductRepository productRepository;

    public EntityResponse<ProductPrice> createProductPrice(Long productId, Long locationId, Double sellingPrice, String effectiveFrom) {
        EntityResponse<ProductPrice> response = new EntityResponse<>();

        try {
            boolean configExists = productPriceRepo.existsByProductIdAndLocationId(productId, locationId);

            if (configExists) {
                response.setMessage("Price for product already exists");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }

            Optional<Product> optionalProduct = productRepository.findById(productId);
            Optional<PickUpLocations> locationOptional = pickUpLocationsRepo.findById(locationId);

            if (optionalProduct.isEmpty() || locationOptional.isEmpty()) {
                response.setMessage("both center and product are required");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return  response;
            }

            Product product = optionalProduct.get();
            PickUpLocations pickUpLocations = locationOptional.get();

            LocalDate localDate = LocalDate.parse(effectiveFrom);
            Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Date date = Date.from(instant);


            //create new product price object, set parameters
            ProductPrice productPrice = new ProductPrice();
            productPrice.setBuyingPrice(product.getPrice());
            productPrice.setSellingPrice(sellingPrice);
            productPrice.setEffectiveFrom(date);
            productPrice.setCreatedOn(new Date());
            productPrice.setProductId(productId);
            productPrice.setLocationId(locationId);

            productPriceRepo.save(productPrice);

            response.setMessage("Price for "+product.getName()+" , "+pickUpLocations.getName()+" added successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(productPrice);
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("An error occurred");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    public EntityResponse<?> getAllProductPrices() {
        EntityResponse<List<ProductPriceRepository.ProductPriceInterface>> response = new EntityResponse<>();

        try {
            List<ProductPriceRepository.ProductPriceInterface> productPrices = productPriceRepo.getMccProductPrices();

            if (productPrices.isEmpty()) {
                response.setMessage("No product prices found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(productPrices);
            }

            response.setMessage("Prices retrieved successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(productPrices);
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("Bad request");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    public EntityResponse<ProductPrice> updateProductPrice(Long productId, Long locationId,Double sellingPrice) {
        EntityResponse<ProductPrice> response = new EntityResponse<>();

        try {
            boolean configExists = productPriceRepo.existsByProductIdAndLocationId(productId, locationId);

            if (!configExists) {
                response.setMessage("Price for product doesn't exist");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }

            log.info("updating existing product prices ............");
            Optional<ProductPrice> priceOptional = productPriceRepo.findByProductIdAndLocationId(productId, locationId);

            if (priceOptional.isEmpty()) {
                response.setMessage("Price for product doesn't exist");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }


            ProductPrice productPrice = priceOptional.get();
            productPrice.setEffectiveFrom(new Date());
            productPrice.setSellingPrice(sellingPrice);

            Optional<Product> optionalProduct = productRepository.findById(productId);
            Optional<PickUpLocations> locationOptional = pickUpLocationsRepo.findById(locationId);

            if (optionalProduct.isEmpty() || locationOptional.isEmpty()) {
                response.setMessage("both center and product are required");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return  response;
            }

            Product product = optionalProduct.get();
            PickUpLocations pickUpLocations = locationOptional.get();


            //update product price object, set parameters
            productPriceRepo.save(productPrice);

            response.setMessage("Price for "+product.getName()+" , "+pickUpLocations.getName()+" updated successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(productPrice);
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("An error occurred");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }


}
