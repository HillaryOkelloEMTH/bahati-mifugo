package com.emtech.dairyapp.Stock.Category;

import com.emtech.dairyapp.Stock.Data.Http.Response.Category.CategoryData;
import com.emtech.dairyapp.Stock.Data.Http.Response.Category.CategoriesResponse;
import com.emtech.dairyapp.Stock.Data.Http.Response.Category.CategoryResponse;
import com.emtech.dairyapp.Stock.Data.Http.Response.StockEntitiesResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Service
@Log
public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;
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

            response.set(StockEntitiesResponse.builder().message("Category added successfully ").statusCode(HttpStatus.OK.value()).build());
        });

        return response.get();
    }

    public StockEntitiesResponse updateCategory(@NonNull String name, @NonNull String description){
        AtomicReference<StockEntitiesResponse> response = new AtomicReference<>();

        this.categoryRepo.findByName(name).ifPresentOrElse(category -> {
            AtomicReference<Category> categoryData = new AtomicReference<>(category);
            categoryData.get().setName(name);
            categoryData.get().setDescription(description);

            categoryData.set(this.categoryRepo.save(categoryData.get()));

            response.set(StockEntitiesResponse.builder().message("Category details modified successfully").statusCode(HttpStatus.OK.value()).build());
        }, () -> {
            log.log(Level.WARNING, String.format("Category with the name %s not found ", name));

            response.set(StockEntitiesResponse.builder().message(String.format("Category with the name %s not found ", name)).statusCode(HttpStatus.BAD_REQUEST.value()).build());
        });

        return response.get();
    }

    public StockEntitiesResponse updateCategoryStatus(@NonNull Long id, @NonNull Integer status){
        AtomicReference<StockEntitiesResponse> response = new AtomicReference<>();

        this.categoryRepo.findById(id).ifPresentOrElse(category -> {
            AtomicReference<Category> categoryData = new AtomicReference<>(category);
            categoryData.get().setStatus(status);

            categoryData.set(this.categoryRepo.save(categoryData.get()));

            response.set(StockEntitiesResponse.builder().message("Category details modified successfully").statusCode(HttpStatus.OK.value()).build());
        }, () -> {
            log.log(Level.WARNING, String.format("Category with the id %s not found ", id));

            response.set(StockEntitiesResponse.builder().message(String.format("Category with the id %s not found ", id)).statusCode(HttpStatus.BAD_REQUEST.value()).build());
        });

        return response.get();
    }

    public CategoryResponse findCategoryById(@NonNull Long id){
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
                categoriesResponse.add(categoryData);
            });

            response.set(CategoriesResponse.builder().categoryData(categoriesResponse).statusCode(HttpStatus.OK.value()).build());
        }

        return response.get();
    }

}
