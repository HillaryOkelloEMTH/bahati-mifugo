package com.emtech.dairyapp.Stock.Product;


import com.emtech.dairyapp.Auth.UserRole.UserRole;
import com.emtech.dairyapp.Stock.Category.Category;
import com.emtech.dairyapp.Stock.Category.CategoryRepo;
import com.emtech.dairyapp.Stock.CategoryProduct.CategoryProduct;
import com.emtech.dairyapp.Stock.CategoryProduct.CategoryProductRepository;
import com.emtech.dairyapp.Stock.Data.Http.Response.Product.ProductData;
import com.emtech.dairyapp.Stock.Data.Http.Response.Product.ProductResponse;
import com.emtech.dairyapp.Stock.Data.Http.Response.Product.ProductsResponse;
import com.emtech.dairyapp.Stock.Data.Http.Response.StockEntitiesResponse;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Service
@Log
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryProductRepository categoryProductRepository;

    @Autowired
    private CategoryRepo categoryRepo;

    public StockEntitiesResponse createProduct(@NonNull String name, @NonNull String description, @NonNull Double price, @NonNull Double salePrice, @NonNull Integer stock, @NonNull Long categoryId){
        AtomicReference<StockEntitiesResponse> response = new AtomicReference<>();

        this.categoryRepo.findById(categoryId).ifPresentOrElse(category -> {
            AtomicReference<Product> product = new AtomicReference<>(new Product());
            product.get().setName(name);
            product.get().setDescription(description);
            product.get().setPrice(price);
            product.get().setStock(stock);
            product.get().setDeleted(0);
            product.get().setSalePrice(salePrice);

            if (salePrice > price){
                product.get().setDiscounted(0);

                double profit = salePrice - price;

                product.get().setProfit(profit);
                product.get().setDiscount(0.0);
            }

            if(price > salePrice){
                product.get().setDiscounted(1);

                double discount = price - salePrice;

                product.get().setDiscount(discount);
                product.get().setProfit(0.0);
            }

            product.set(this.productRepository.save(product.get()));

            if(this.assignCategory(category, product.get())){
                response.set(StockEntitiesResponse.builder().message("Product added successfully ").statusCode(HttpStatus.OK.value()).build());
            }else {
                response.set(StockEntitiesResponse.builder().message("Sorry an error occurred, please try again later ").statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
            }
        }, () -> {
            response.set(StockEntitiesResponse.builder().message(String.format("Category with id %s not found  ", categoryId)).statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
        });



        return response.get();
    }

    public boolean assignCategory(@NonNull Category category, @NonNull Product product ){
        AtomicBoolean res = new AtomicBoolean();

        this.productRepository.findById(product.getId()).ifPresentOrElse(productData -> {
            if (productData.getDeleted().compareTo(1) < 1){
                this.categoryRepo.findById(category.getId()).ifPresentOrElse(myCategory -> {
                    if (myCategory.getStatus().compareTo(1) == 0){
                        this.categoryProductRepository.findByCategoryAndProduct(myCategory, productData).ifPresentOrElse(cp -> {
                            log.log(Level.INFO, "Product is already assigned to this category");

                            res.set(true);
                        }, () -> {
                            AtomicReference<CategoryProduct> categoryProduct = new AtomicReference<>(new CategoryProduct());
                            categoryProduct.get().setProduct(productData);
                            categoryProduct.get().setCategory(category);

                            categoryProduct.set(this.categoryProductRepository.save(categoryProduct.get()));

                            log.log(Level.INFO, String.format("Category Product created [ %s ]", categoryProduct.get()));

                            res.set(true);

                        });
                    }
                }, () -> {
                    log.log(Level.INFO, "Category not found");

                    res.set(false);
                });
            }
        }, () -> {
            log.log(Level.INFO, "Product not found");

            res.set(false);
        });

        return res.get();
    }

    public StockEntitiesResponse updateProduct(@NonNull Long productId, @NonNull String name, @NonNull String description, @NonNull Double price, @NonNull Double salePrice){
        AtomicReference<StockEntitiesResponse> response = new AtomicReference<>();

        this.productRepository.findById(productId).ifPresentOrElse(product -> {
            AtomicReference<Product> productData = new AtomicReference<>(product);
            productData.get().setName(name);
            productData.get().setDescription(description);
            productData.get().setPrice(price);
            productData.get().setSalePrice(salePrice);

            if (salePrice > price){
                productData.get().setDiscounted(0);

                double profit = salePrice - price;

                productData.get().setProfit(profit);
                productData.get().setDiscount(0.0);
            }

            if(price > salePrice){
                productData.get().setDiscounted(1);

                double discount = price - salePrice;

                productData.get().setDiscount(discount);
                productData.get().setProfit(0.0);
            }

            productData.set(this.productRepository.save(productData.get()));

            response.set(StockEntitiesResponse.builder().message("Product updated successfully ").statusCode(HttpStatus.OK.value()).build());
        }, () -> {
            log.log(Level.WARNING, String.format("Product with the id %s not found ", productId));

            response.set(StockEntitiesResponse.builder().message(String.format("Product with the id %s not found ", productId)).statusCode(HttpStatus.BAD_REQUEST.value()).build());
        });

        return response.get();
    }

    public StockEntitiesResponse updateProductStatus(@NonNull Long productId, @NonNull Integer deleted){
        AtomicReference<StockEntitiesResponse> response = new AtomicReference<>();

        this.productRepository.findById(productId).ifPresentOrElse(product -> {
            AtomicReference<Product> productData = new AtomicReference<>(product);
            productData.get().setDeleted(deleted);

            productData.set(this.productRepository.save(productData.get()));

            response.set(StockEntitiesResponse.builder().message("Product updated successfully ").statusCode(HttpStatus.OK.value()).build());
        }, () -> {
            log.log(Level.WARNING, String.format("Product with the id %s not found ", productId));

            response.set(StockEntitiesResponse.builder().message(String.format("Product with the id %s not found ", productId)).statusCode(HttpStatus.BAD_REQUEST.value()).build());
        });

        return response.get();
    }

    public ProductResponse getProductDetails(@NonNull Long productId){
        AtomicReference<ProductResponse> response = new AtomicReference<>();

        this.productRepository.findById(productId).ifPresentOrElse(product -> {
            ProductData productData = ProductData.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .salePrice(product.getSalePrice())
                    .profit(product.getProfit())
                    .discount(product.getDiscount())
                    .discounted(product.getDiscounted())
                    .deleted(product.getDeleted())
                    .creationDate(product.getCreationDate())
                    .updateDate(product.getUpdateDate())
                    .build();

            response.set(ProductResponse.builder().productData(productData).statusCode(HttpStatus.OK.value()).build());

        }, () -> {
            log.log(Level.WARNING, String.format("Product with the id %s not found ", productId));

        });

        return response.get();
    }

    public ProductsResponse findAllProducts(){
        AtomicReference<ProductsResponse> response = new AtomicReference<>();

        List<Product> products = this.productRepository.findAll();

        List<ProductData> productsData = new ArrayList<>();

        if(!products.isEmpty()){
            products.forEach(product -> {
                ProductData productData = ProductData.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .salePrice(product.getSalePrice())
                        .profit(product.getProfit())
                        .discount(product.getDiscount())
                        .type(product.getType())
                        .discounted(product.getDiscounted())
                        .deleted(product.getDeleted())
                        .stock(product.getStock())
                        .creationDate(product.getCreationDate())
                        .updateDate(product.getUpdateDate())
                        .build();

                productsData.add(productData);
            });

            response.set(ProductsResponse.builder().productData(productsData).statusCode(HttpStatus.OK.value()).build());
        }else {
            log.log(Level.INFO, "Products not found ");
        }

        return response.get();
    }

    public ProductsResponse findAllProductsByStatus(@NonNull Integer status){
        AtomicReference<ProductsResponse> response = new AtomicReference<>();

        List<Product> products = this.productRepository.findAllByDeleted(status);

        List<ProductData> productsData = new ArrayList<>();

        if(!products.isEmpty()){
            products.forEach(product -> {
                ProductData productData = ProductData.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .salePrice(product.getSalePrice())
                        .profit(product.getProfit())
                        .discount(product.getDiscount())
                        .discounted(product.getDiscounted())
                        .deleted(product.getDeleted())
                        .creationDate(product.getCreationDate())
                        .updateDate(product.getUpdateDate())
                        .build();

                productsData.add(productData);
            });

            response.set(ProductsResponse.builder().productData(productsData).statusCode(HttpStatus.OK.value()).build());
        }else {
            log.log(Level.INFO, "Products not found ");
        }

        return response.get();
    }

    public StockEntitiesResponse deleteProduct(@NonNull Long productId){
        AtomicReference<StockEntitiesResponse> response = new AtomicReference<>();

        this.productRepository.findById(productId).ifPresentOrElse(product -> {
           this.productRepository.deleteById(productId);

            response.set(StockEntitiesResponse.builder().message("Product deleted successfully ").statusCode(HttpStatus.OK.value()).build());
        }, () -> {
            log.log(Level.WARNING, String.format("Product with the id %s not found ", productId));

            response.set(StockEntitiesResponse.builder().message(String.format("Product with the id %s not found ", productId)).statusCode(HttpStatus.BAD_REQUEST.value()).build());
        });

        return response.get();
    }
}
