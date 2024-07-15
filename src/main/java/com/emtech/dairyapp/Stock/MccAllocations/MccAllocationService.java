package com.emtech.dairyapp.Stock.MccAllocations;

import com.emtech.dairyapp.Auth.User.User;
import com.emtech.dairyapp.Auth.User.UserRepository;
import com.emtech.dairyapp.Configurations.MccProductPrices.ProductPrice;
import com.emtech.dairyapp.Configurations.MccProductPrices.ProductPriceRepository;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocations;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocationsRepo;
import com.emtech.dairyapp.Response.EntityResponse;
import com.emtech.dairyapp.Stock.Data.Http.Response.Product.ProductData;
import com.emtech.dairyapp.Stock.Data.Http.Response.Product.ProductResponse;
import com.emtech.dairyapp.Stock.Data.Http.Response.Product.ProductsResponse;
import com.emtech.dairyapp.Stock.Product.Product;
import com.emtech.dairyapp.Stock.Product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class MccAllocationService {
    @Autowired
    private MccAllocationRepo mccAllocationRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductPriceRepository priceRepository;

    @Autowired
    private PickUpLocationsRepo pickUpLocationsRepo;

    public EntityResponse<?> allocateProducts(Long productId, Long locationId,Integer stock) {
        EntityResponse<String> response = new EntityResponse<>();

        try {
            Optional<PickUpLocations> optionalLocation = pickUpLocationsRepo.findById(locationId);
            Optional<Product> optionalProduct = productRepository.findById(productId);
            boolean priceConfig = priceRepository.existsByProductIdAndLocationId(productId, locationId);

            log.info("checking if mcc with id {} exists .....", locationId);
            if (optionalLocation.isEmpty()) {
                response.setMessage("pickup center not found");
                response.setEntity("Not Found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return response;
            }

            log.info("checking if product with id {} exists .......", productId);
            if (optionalProduct.isEmpty()) {
                response.setMessage("Product details not found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("Not Found");
                return response;
            }

            PickUpLocations pickUpLocations = optionalLocation.get();
            Product product = optionalProduct.get();


            log.info("checking if quantity requested is above current stock -----");
            if (stock > product.getStock()) {
                response.setMessage("Quantity requested is above current stock");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("Order lower stock");
                return response;
            }

            log.info("checking if the product price for {} in {} collection center is set ......", product.getName(), pickUpLocations.getName());
            if (!priceConfig) {
                response.setMessage("Sell prices for "+product.getName()+" in "+pickUpLocations.getName()+" not set");
                response.setStatusCode(HttpStatus.FORBIDDEN.value());
                response.setEntity("Selling price absent");
                return response;
            }

            log.info("Allocating {} of {} to {} MCC on {} ............", stock, product.getName(), pickUpLocations.getName(), LocalDateTime.now());
            MccAllocation mccAllocation = new MccAllocation();
            mccAllocation.setProductId(productId);
            mccAllocation.setLocationId(locationId);
            mccAllocation.setStock(stock);
            mccAllocation.setAllocatedOn(new Date());

            mccAllocationRepo.save(mccAllocation);

            log.info("updating the product stock count in the inventory .......");
            Integer newStockCount = product.getStock() - stock;
            product.setStock(newStockCount);
            productRepository.save(product);

            response.setMessage(stock+" of "+product.getName()+" allocated to "+pickUpLocations.getName());
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity("Allocation Successful");
        } catch (Exception e) {
            log.error(e.toString());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Bad Request");
        }
        return response;
    }

    public ProductsResponse getMccProducts(Long locationId) {
        AtomicReference<ProductsResponse> response = new AtomicReference<>();

        try {
            List<MccAllocationRepo.MccProducts> mccProductsList = mccAllocationRepo.getMccProducts(locationId);
            List<ProductData> productData = new ArrayList<>();

            if (mccProductsList.isEmpty()) {
                response.set(ProductsResponse.builder().message("No product allocations found").statusCode(HttpStatus.NOT_FOUND.value()).productData(productData).build());
                return response.get();
            }

            mccProductsList.stream().filter(mccProduct -> mccProduct.getStock() >= 1).forEach(mccProduct -> {
                ProductData product = ProductData.builder()
                        .id(mccProduct.getProduct_id())
                        .stock(mccProduct.getStock())
                        .name(mccProduct.getName())
                        .category(mccProduct.getCategory())
                        .salePrice(mccProduct.getSelling_price())
                        .description(mccProduct.getDescription())
                        .mcc(mccProduct.getMcc())
                        .type(mccProduct.getType())
                        .categoryId(mccProduct.getCategory_id())
                        .build();
                productData.add(product);
            } );

            response.set(ProductsResponse.builder().message("Product allocations found").statusCode(HttpStatus.OK.value()).productData(productData).build());
        } catch (Exception e) {
            log.error(e.toString());
            response.set(ProductsResponse.builder().message("Bad Request").statusCode(HttpStatus.BAD_REQUEST.value()).build());
        }
        return response.get();
    }

    public ProductsResponse getAllMccProducts() {
        AtomicReference<ProductsResponse> response = new AtomicReference<>();

        try {
            List<MccAllocationRepo.MccProducts> mccProductsList = mccAllocationRepo.getAllMccProducts();
            List<ProductData> productData = new ArrayList<>();

            if (mccProductsList.isEmpty()) {
                response.set(ProductsResponse.builder().message("No product allocations found").statusCode(HttpStatus.OK.value()).productData(productData).build());
                return response.get();
            }

            mccProductsList.stream().filter(mccProduct -> mccProduct.getStock() >= 1).forEach(mccProduct -> {
                ProductData product = ProductData.builder()
                        .id(mccProduct.getProduct_id())
                        .stock(mccProduct.getStock())
                        .name(mccProduct.getName())
                        .category(mccProduct.getCategory())
                        .price(mccProduct.getPrice())
                        .salePrice(mccProduct.getSelling_price())
                        .allocatedOn(mccProduct.getAllocated_on())
                        .description(mccProduct.getDescription())
                        .mcc(mccProduct.getMcc())
                        .build();
                productData.add(product);
            } );

            response.set(ProductsResponse.builder().message("Product allocations found").statusCode(HttpStatus.OK.value()).productData(productData).build());
        } catch (Exception e) {
            log.error(e.toString());
            response.set(ProductsResponse.builder().message("Bad Request").statusCode(HttpStatus.BAD_REQUEST.value()).build());
        }
        return response.get();
    }
}
