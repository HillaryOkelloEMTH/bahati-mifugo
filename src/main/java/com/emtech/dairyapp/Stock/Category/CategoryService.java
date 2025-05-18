package com.emtech.dairyapp.Stock.Category;

import com.emtech.dairyapp.Stock.CategoryProduct.CategoryProduct;
import com.emtech.dairyapp.Stock.CategoryProduct.CategoryProductRepository;
import com.emtech.dairyapp.Stock.Data.Http.Response.Category.CategoryData;
import com.emtech.dairyapp.Stock.Data.Http.Response.Category.CategoriesResponse;
import com.emtech.dairyapp.Stock.Data.Http.Response.Category.CategoryResponse;
import com.emtech.dairyapp.Stock.Data.Http.Response.Product.ProductData;
import com.emtech.dairyapp.Stock.Data.Http.Response.StockEntitiesResponse;
import com.emtech.dairyapp.Stock.Product.audit.AuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import static com.emtech.dairyapp.Auth.Utilities.UserInfo.username;

@Service
@Log
public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private CategoryProductRepository categoryProductRepository;
    @Autowired
    private AuditService auditService;


    public StockEntitiesResponse createCategory(@NonNull String name, @NonNull String description){
        AtomicReference<StockEntitiesResponse> response = new AtomicReference<>();

        this.categoryRepo.findByName(name).ifPresentOrElse(category -> {
            log.log(Level.SEVERE, String.format("Category with the name %s already exists", name));

            response.set(StockEntitiesResponse.builder().message(String.format("Category with the name %s already exists", name)).statusCode(HttpStatus.BAD_REQUEST.value()).build());
        }, () -> {
            AtomicReference<Category> category = new AtomicReference<>(new Category());
            category.get().setName(name);
            category.get().setDescription(description);
            category.get().setStatus(1);

            category.set(this.categoryRepo.save(category.get()));
            auditService.logAction("POST", "Category", category.get(), "New category created");

            response.set(StockEntitiesResponse.builder().message("Category added successfully ").statusCode(HttpStatus.OK.value()).build());
        });

        return response.get();
    }

    @Transactional
    public StockEntitiesResponse updateCategory(@NonNull String name, @NonNull String description, Long categoryId) {

        AtomicReference<StockEntitiesResponse> response = new AtomicReference<>();

        this.categoryRepo.findById(categoryId).ifPresent(existingCategory -> {
            Category beforeUpdate = new Category();
            beforeUpdate.setId(existingCategory.getId());
            beforeUpdate.setName(existingCategory.getName());
            beforeUpdate.setDescription(existingCategory.getDescription());
            beforeUpdate.setStatus(existingCategory.getStatus());

            existingCategory.setName(name);
            existingCategory.setDescription(description);

            Category afterUpdate = this.categoryRepo.save(existingCategory);

            auditService.logUpdateAction("Category", beforeUpdate, afterUpdate);

            response.set(StockEntitiesResponse.builder()
                    .message("Category details modified successfully")
                    .statusCode(HttpStatus.OK.value())
                    .build());
        });

        return response.get() != null ? response.get() : StockEntitiesResponse.builder()
                .message("No updates were made, category may not exist")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    public StockEntitiesResponse recoverDeletedCategory(@NonNull Long categoryId) {
        AtomicReference<StockEntitiesResponse> response = new AtomicReference<>();

        this.categoryRepo.findById(categoryId).ifPresentOrElse(category -> {
            // Clone the existing category to capture its state before update
            Category beforeUpdate = new Category();
            beforeUpdate.setId(category.getId());
            beforeUpdate.setName(category.getName());
            beforeUpdate.setDescription(category.getDescription());
            beforeUpdate.setStatus(category.getStatus());
            beforeUpdate.setDeletedFlag(category.getDeletedFlag());
            beforeUpdate.setCreationDate(category.getCreationDate());
            beforeUpdate.setUpdateDate(category.getUpdateDate());

            category.setDeletedFlag("Active");
            category.setUpdateDate(Timestamp.from(ZonedDateTime.now().toInstant()));

            Category afterUpdate = this.categoryRepo.save(category);

            auditService.logUpdateAction("Category", beforeUpdate, afterUpdate);

            response.set(StockEntitiesResponse.builder()
                    .message("Category recovered successfully")
                    .statusCode(HttpStatus.OK.value())
                    .build());
        }, () -> {
            log.log(Level.WARNING, String.format("Category with the id %s not found ", categoryId));

            response.set(StockEntitiesResponse.builder()
                    .message(String.format("Category with the id %s not found ", categoryId))
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build());
        });

        return response.get();
    }


    public CategoryResponse findCategoryById( Long id){
        AtomicReference<CategoryResponse> response = new AtomicReference<>();

        this.categoryRepo.findById(id).ifPresentOrElse(category -> {
            CategoryData categoryData = CategoryData.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .status(category.getStatus())
                    .creationDate(category.getCreationDate())
                    .updateDate(category.getUpdateDate())
                    .build();

            List<ProductData> products = new ArrayList<>();

            List<CategoryProduct> categoryProducts = this.categoryProductRepository.findAllByCategory(category);

            if(categoryProducts != null && !categoryProducts.isEmpty()){
                categoryProducts.forEach(categoryProduct -> {
                    ProductData product = ProductData.builder()
                            .id(categoryProduct.getProduct().getId())
                            .name(categoryProduct.getProduct().getName())
                            .description(categoryProduct.getProduct().getDescription())
                            .price(categoryProduct.getProduct().getPrice())
                            .salePrice(categoryProduct.getProduct().getSalePrice())
                            .profit(categoryProduct.getProduct().getProfit())
                            .discount(categoryProduct.getProduct().getDiscount())
                            .discounted(categoryProduct.getProduct().getDiscounted())
                            .deleted(categoryProduct.getProduct().getDeleted())
                            .updateDate(categoryProduct.getProduct().getUpdateDate())
                            .creationDate(categoryProduct.getProduct().getCreationDate())
                            .build();

                    products.add(product);
                });

//                categoryData.setProducts(products);
            }

            response.set(CategoryResponse.builder().categoryData(categoryData).statusCode(HttpStatus.OK.value()).build());
        }, () -> {
            log.log(Level.WARNING, String.format("Category with the id %s not found ", id));
        });

        return response.get();
    }


    public CategoryResponse findCategoryByName(@NonNull String name){
        AtomicReference<CategoryResponse> response = new AtomicReference<>();

        this.categoryRepo.findByName(name).ifPresentOrElse(category -> {
            CategoryData categoryData = CategoryData.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .status(category.getStatus())
                    .creationDate(category.getCreationDate())
                    .updateDate(category.getUpdateDate())
                    .build();

            List<ProductData> products = new ArrayList<>();

            List<CategoryProduct> categoryProducts = this.categoryProductRepository.findAllByCategory(category);

            if(categoryProducts != null && !categoryProducts.isEmpty()){
                categoryProducts.forEach(categoryProduct -> {
                    ProductData product = ProductData.builder()
                            .id(categoryProduct.getProduct().getId())
                            .name(categoryProduct.getProduct().getName())
                            .description(categoryProduct.getProduct().getDescription())
                            .price(categoryProduct.getProduct().getPrice())
                            .salePrice(categoryProduct.getProduct().getSalePrice())
                            .profit(categoryProduct.getProduct().getProfit())
                            .discount(categoryProduct.getProduct().getDiscount())
                            .discounted(categoryProduct.getProduct().getDiscounted())
                            .deleted(categoryProduct.getProduct().getDeleted())
                            .updateDate(categoryProduct.getProduct().getUpdateDate())
                            .creationDate(categoryProduct.getProduct().getCreationDate())
                            .build();

                    products.add(product);
                });

//                categoryData.setProducts(products);
            }

            response.set(CategoryResponse.builder().categoryData(categoryData).statusCode(HttpStatus.OK.value()).build());
        }, () -> {
            log.log(Level.WARNING, String.format("Category with the name %s not found ", name));
        });

        return response.get();
    }

    public CategoriesResponse findAllCategories(){
        AtomicReference<CategoriesResponse> response = new AtomicReference<>();

        List<Category> categories = this.categoryRepo.findAll();
        List<CategoryData> categoriesResponse = new ArrayList<>();

        if(!categories.isEmpty()){
            categories.forEach(category -> {
                CategoryData categoryData = CategoryData.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .status(category.getStatus())
                        .updateDate(category.getUpdateDate())
                        .creationDate(category.getCreationDate())
                        .build();


                List<ProductData> products = new ArrayList<>();

                List<CategoryProduct> categoryProducts = this.categoryProductRepository.findAllByCategory(category);

                if(categoryProducts != null && !categoryProducts.isEmpty()){
                    categoryProducts.forEach(categoryProduct -> {
                        ProductData product = ProductData.builder()
                                .id(categoryProduct.getProduct().getId())
                                .name(categoryProduct.getProduct().getName())
                                .description(categoryProduct.getProduct().getDescription())
                                .price(categoryProduct.getProduct().getPrice())
                                .salePrice(categoryProduct.getProduct().getSalePrice())
                                .profit(categoryProduct.getProduct().getProfit())
                                .discount(categoryProduct.getProduct().getDiscount())
                                .discounted(categoryProduct.getProduct().getDiscounted())
                                .deleted(categoryProduct.getProduct().getDeleted())
                                .updateDate(categoryProduct.getProduct().getUpdateDate())
                                .creationDate(categoryProduct.getProduct().getCreationDate())
                                .build();

                        products.add(product);
                    });
                    System.out.println("category"+category);
//                    categoryData.setProducts(products);
                }
                System.out.println("this is the category data "+categoriesResponse);


                categoriesResponse.add(categoryData);
            });

            response.set(CategoriesResponse.builder().categoryData(categoriesResponse).statusCode(HttpStatus.OK.value()).build());
        }

        return response.get();
    }

    public CategoriesResponse findCategoriesByStatus(@NonNull Integer status){
        AtomicReference<CategoriesResponse> response = new AtomicReference<>();

        List<Category> categories = this.categoryRepo.findByStatus(status);

        List<CategoryData> categoriesResponse = new ArrayList<>();

        if(!categories.isEmpty()){
            categories.forEach(category -> {
                CategoryData categoryData = CategoryData.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .status(category.getStatus())
                        .updateDate(category.getUpdateDate())
                        .creationDate(category.getCreationDate())
                        .build();

                List<ProductData> products = new ArrayList<>();

                List<CategoryProduct> categoryProducts = this.categoryProductRepository.findAllByCategory(category);

                if(categoryProducts != null && !categoryProducts.isEmpty()){
                    categoryProducts.forEach(categoryProduct -> {
                        ProductData product = ProductData.builder()
                                .id(categoryProduct.getProduct().getId())
                                .name(categoryProduct.getProduct().getName())
                                .description(categoryProduct.getProduct().getDescription())
                                .price(categoryProduct.getProduct().getPrice())
                                .salePrice(categoryProduct.getProduct().getSalePrice())
                                .profit(categoryProduct.getProduct().getProfit())
                                .discount(categoryProduct.getProduct().getDiscount())
                                .discounted(categoryProduct.getProduct().getDiscounted())
                                .deleted(categoryProduct.getProduct().getDeleted())
                                .updateDate(categoryProduct.getProduct().getUpdateDate())
                                .creationDate(categoryProduct.getProduct().getCreationDate())
                                .build();

                        products.add(product);
                    });

//                    categoryData.setProducts(products);
                }


                categoriesResponse.add(categoryData);
            });

            response.set(CategoriesResponse.builder().categoryData(categoriesResponse).statusCode(HttpStatus.OK.value()).build());
        }

        return response.get();
    }

    public StockEntitiesResponse deleteCategory(Long categoryId) {
        return categoryRepo.findById(categoryId)
                .map(existingCategory -> {

                    Category beforeUpdate = new Category();

                    beforeUpdate.setId(existingCategory.getId());
                    beforeUpdate.setName(existingCategory.getName());
                    beforeUpdate.setDescription(existingCategory.getDescription());
                    beforeUpdate.setStatus(existingCategory.getStatus());
                    beforeUpdate.setDeletedFlag(existingCategory.getDeletedFlag());
                    existingCategory.setDeletedFlag("DELETED");

                    Category afterUpdate = categoryRepo.save(existingCategory);

                    auditService.logUpdateAction("Category", beforeUpdate, afterUpdate);

                    return StockEntitiesResponse.builder()
                            .message("Category flagged as deleted successfully")
                            .statusCode(HttpStatus.OK.value())
                            .build();
                })
                .orElseGet(() -> StockEntitiesResponse.builder()
                        .message("Category not found")
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .build());
    }


}
