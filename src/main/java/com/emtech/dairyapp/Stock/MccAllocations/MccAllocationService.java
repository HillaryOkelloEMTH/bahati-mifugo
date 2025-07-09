package com.emtech.dairyapp.Stock.MccAllocations;

import com.emtech.dairyapp.Auth.User.User;
import com.emtech.dairyapp.Auth.User.UserRepository;
import com.emtech.dairyapp.Configurations.MccProductPrices.ProductPrice;
import com.emtech.dairyapp.Configurations.MccProductPrices.ProductPriceRepository;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocations;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocationsRepo;
import com.emtech.dairyapp.Configurations.Utils.Formatter;
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
            Optional<MccAllocation> allocationOptional = mccAllocationRepo.findByProductIdAndLocationId(productId, locationId);

            log.info("checking if mcc with id {} exists .....", locationId);
            if (optionalLocation.isEmpty()) {
                response.setMessage("pickup center not found");
                response.setEntity("Not Found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return response;
            }

            log.info("checking if product with id {} exists .....", productId);
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

            Integer newStockCount = product.getStock() - stock;

            log.info("checking if the product is already allocated to {} collection center ......",pickUpLocations.getName());
            if (allocationOptional.isPresent()) {
                MccAllocation existingAllocation = allocationOptional.get();
                existingAllocation.setStock(existingAllocation.getStock()+stock);
                existingAllocation.setUpdatedOn(new Date());

                log.info("updating the product stock count in the inventory .......");
                product.setStock(newStockCount);
                productRepository.save(product);
                mccAllocationRepo.save(existingAllocation);

                response.setMessage(stock+" units of "+product.getName()+" allocated to "+pickUpLocations.getName());
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("Allocation Successful");
                return response;
            }


            log.info("updating the product stock count in the inventory .......");
            MccAllocation mccAllocation = new MccAllocation();
            mccAllocation.setProductId(productId);
            mccAllocation.setLocationId(locationId);
            mccAllocation.setStock(stock);
            mccAllocation.setAllocatedOn(new Date());
            product.setStock(newStockCount);
            productRepository.save(product);

            log.info("Allocating {} units of {} to {} MCC on {} ............", stock, product.getName(), pickUpLocations.getName(), Formatter.formatDate(new Date()));
            mccAllocationRepo.save(mccAllocation);

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

//    Get Specific Center's product allocation.
    public ProductsResponse getMccProducts(Long locationId) {
        AtomicReference<ProductsResponse> response = new AtomicReference<>();

        try {
            List<MccAllocationRepo.MccProducts> mccProductsList = mccAllocationRepo.getMccProducts(locationId);
            List<ProductData> productData = new ArrayList<>();

            if (mccProductsList.isEmpty()) {
                response.set(ProductsResponse.builder().message("No product allocations found").statusCode(HttpStatus.NOT_FOUND.value()).productData(productData).build());
                return response.get();
            }

            mccProductsList.forEach(mccProduct -> {
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
                        .allocatedOn(mccProduct.getAllocated_on())
                        .creationDate(mccProduct.getCreation_date())
                        .updateDate(mccProduct.getUpdate_date())
                        .deleted(mccProduct.getDeleted())
                        .discount(mccProduct.getDiscount())
                        .discounted(mccProduct.getDiscounted())
                        .price(mccProduct.getPrice())
                        .priceType(mccProduct.getPrice_type())
                        .profit(mccProduct.getProfit())
                        .build();
                productData.add(product);
            } );

            response.set(ProductsResponse.builder().message(productData.size()+" Product allocations found").statusCode(HttpStatus.OK.value()).productData(productData).build());
        } catch (Exception e) {
            log.error(e.toString());
            response.set(ProductsResponse.builder().message("Bad Request").statusCode(HttpStatus.BAD_REQUEST.value()).build());
        }
        return response.get();
    }
//    Get all Products in all Centers.
    public ProductsResponse getAllMccProducts() {
        AtomicReference<ProductsResponse> response = new AtomicReference<>();

        try {
            List<MccAllocationRepo.MccProducts> mccProductsList = mccAllocationRepo.getAllMccProducts();
            List<ProductData> productData = new ArrayList<>();

            if (mccProductsList.isEmpty()) {
                response.set(ProductsResponse.builder().message("No product allocations found").statusCode(HttpStatus.OK.value()).productData(productData).build());
                return response.get();
            }

            mccProductsList.forEach(mccProduct -> {
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
                        .allocatedOn(mccProduct.getAllocated_on())
                        .creationDate(mccProduct.getCreation_date())
                        .updateDate(mccProduct.getUpdate_date())
                        .deleted(mccProduct.getDeleted())
                        .discount(mccProduct.getDiscount())
                        .discounted(mccProduct.getDiscounted())
                        .price(mccProduct.getPrice())
                        .priceType(mccProduct.getPrice_type())
                        .profit(mccProduct.getProfit())
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


    public EntityResponse<?> stockTransfer(Long sourceId, Long destinationId, Long productId, Integer stock) {
        EntityResponse<String> response = new EntityResponse<>();

        try {
            Optional<PickUpLocations> optionalLocationA = pickUpLocationsRepo.findById(sourceId);
            Optional<PickUpLocations> optionalLocationB = pickUpLocationsRepo.findById(destinationId);

            Optional<Product> optionalProduct = productRepository.findById(productId);
            boolean priceConfigA = priceRepository.existsByProductIdAndLocationId(productId, sourceId);
            boolean priceConfigB = priceRepository.existsByProductIdAndLocationId(productId, destinationId);

            Optional<MccAllocation> allocationOptionalA = mccAllocationRepo.findByProductIdAndLocationId(productId, sourceId);
            Optional<MccAllocation> allocationOptionalB = mccAllocationRepo.findByProductIdAndLocationId(productId, destinationId);

            log.info("checking if mcc's with id's {} and {} exists .....", sourceId, destinationId);
            if (optionalLocationA.isEmpty() || optionalLocationB.isEmpty()) {
                response.setMessage("pickup centers not found not found");
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

            PickUpLocations pickUpLocationsA = optionalLocationA.get();
            PickUpLocations pickUpLocationsB = optionalLocationB.get();

            log.info("checking the current stock of the product in the source destination .....");
            if (allocationOptionalA.isEmpty()) {
                response.setMessage("Product not found for {} "+ pickUpLocationsA.getName());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("Not Found");
                return response;
            }

            MccAllocation mccAllocationA = allocationOptionalA.get();
            Product product = optionalProduct.get();


            log.info("checking if quantity requested is above current stock -----");
            if (stock > mccAllocationA.getStock()) {
                response.setMessage("Quantity requested is above current stock");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("Order lower stock");
                return response;
            }

            log.info("checking if the product price for {} in {} collection center is set ......", product.getName(), pickUpLocationsB.getName());
            if (!priceConfigB) {
                response.setMessage("Sell prices for "+product.getName()+" in "+pickUpLocationsB.getName()+" not set");
                response.setStatusCode(HttpStatus.FORBIDDEN.value());
                response.setEntity("Selling price absent");
                return response;
            }

            Integer newStockCount = mccAllocationA.getStock() - stock;

            log.info("checking if the product is already allocated to {} collection center ......",pickUpLocationsB.getName());
            if (allocationOptionalB.isPresent()) {
                MccAllocation existingAllocation = allocationOptionalB.get();
                existingAllocation.setStock(existingAllocation.getStock() + stock);
                existingAllocation.setUpdatedOn(new Date());

                log.info("updating the product stock count in the inventory .......");
                mccAllocationA.setStock(newStockCount);
                mccAllocationRepo.save(mccAllocationA);
                mccAllocationRepo.save(existingAllocation);

                response.setMessage(stock + " units of " + product.getName() + " transferred to " + pickUpLocationsB.getName());
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("Stock transfer Successful");
                return response;
            }


            log.info("updating the product stock count in the mcc inventory .......");
            MccAllocation mccAllocation = new MccAllocation();
            mccAllocation.setProductId(productId);
            mccAllocation.setLocationId(destinationId);
            mccAllocation.setStock(stock);
            mccAllocation.setAllocatedOn(new Date());
            mccAllocationA.setStock(newStockCount);


            log.info("updating the product stock count in the inventory of source mcc .......");
            mccAllocationA.setStock(newStockCount);
            mccAllocationRepo.save(mccAllocationA);

            log.info("Allocating {} units of {} to {} MCC on {} ............", stock, product.getName(), pickUpLocationsB.getName(), Formatter.formatDate(new Date()));
            mccAllocationRepo.save(mccAllocation);

            response.setMessage(stock+" of "+product.getName()+" transferred to "+pickUpLocationsB.getName());
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity("Stock transfer Successful");
        } catch (Exception e) {
            log.error(e.toString());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Bad Request");
        }
        return response;
    }

//    Filter by Collection Centre, productId and Date Range.
    public ProductsResponse getFilterMccProducts(Long locationId, Long productId, Date startDate, Date endDate){

//        if (month < 1 || month > 12){
//
//        }
        List<MccAllocation> allocations = mccAllocationRepo.findAllByFilters(locationId, productId, startDate, endDate);
        return toProductsResponse(allocations);
    }
//   Fil
    private ProductsResponse toProductsResponse(List<MccAllocation> allocations) {

        List<ProductData> data = allocations.stream().map(a -> {
            Product p = productRepository.findById(a.getProductId()).orElse(null);
            PickUpLocations mcc = pickUpLocationsRepo.findById(a.getLocationId()).orElse(null);

            return ProductData.builder()
                    .id(a.getProductId())
                    .stock(a.getStock())
                    .name(p != null ? p.getName() : null)
                    .mcc(mcc != null ? mcc.getName() : null)
                    .allocatedOn(a.getAllocatedOn())
                    .build();
        }).toList();

        return ProductsResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(data.isEmpty() ? "No allocations found" : data.size() + " allocations found")
                .productData(data)
                .build();
    }

}
