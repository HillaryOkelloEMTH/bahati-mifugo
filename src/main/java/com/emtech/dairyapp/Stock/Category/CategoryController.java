package com.emtech.dairyapp.Stock.Category;

import com.emtech.dairyapp.Auth.Utilities.CurrentUserContext;
import com.emtech.dairyapp.Auth.Utilities.UserInfo;
import com.emtech.dairyapp.Stock.Data.Http.Request.Category.CategoryCreateRequest;
import com.emtech.dairyapp.Stock.Data.Http.Response.Category.CategoriesResponse;
import com.emtech.dairyapp.Stock.Data.Http.Response.Category.CategoryResponse;
import com.emtech.dairyapp.Stock.Data.Http.Response.StockEntitiesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.emtech.dairyapp.Auth.Utilities.UserInfo.username;

@RestController
@RequestMapping(path = "/api/v1/product-categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @RequestMapping(
            path = "/add-category",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<StockEntitiesResponse>> createCategory(@RequestBody CategoryCreateRequest body){
        StockEntitiesResponse response = this.categoryService.createCategory(body.getName(), body.getDescription());

        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()) && response.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR.value()){
           return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

    @RequestMapping(
            path = "/update-category",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<StockEntitiesResponse>> updateCategory(@RequestBody CategoryCreateRequest body, @RequestParam Long categoryId){
        StockEntitiesResponse response = this.categoryService.updateCategory(body.getName(), body.getDescription(), categoryId);

        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()) && response.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR.value()){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }


    @PutMapping("/delete-category/{categoryId}")
    public Mono<ResponseEntity<StockEntitiesResponse>> flagDeleteCategory(@PathVariable Long categoryId) {

        StockEntitiesResponse response = categoryService.deleteCategory(categoryId);

        if (!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()) &&
                response.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return Mono.just(ResponseEntity.ok().body(response));
        } else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

    @PutMapping("/activate-category/{categoryId}")

    public Mono<ResponseEntity<StockEntitiesResponse>> activateCategory(@PathVariable Long categoryId){
        StockEntitiesResponse response = this.categoryService.recoverDeletedCategory(categoryId);

        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()) && response.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR.value()){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }


    @RequestMapping(
            path = "/find-category-by-id/{categoryId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<CategoryResponse>> findCategoryById(@PathVariable Long categoryId){
        CategoryResponse response = this.categoryService.findCategoryById(categoryId);

        return Mono.just(ResponseEntity.ok().body(response));
    }

    @RequestMapping(
            path = "/find-category-by-name/{categoryName}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<CategoryResponse>> findCategoryByName(@PathVariable String categoryName){
        CategoryResponse response = this.categoryService.findCategoryByName(categoryName);

        return Mono.just(ResponseEntity.ok().body(response));
    }

    @RequestMapping(
            path = "/find-all-categories",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<CategoriesResponse>> findAllCategories(){
        CategoriesResponse response = this.categoryService.findAllCategories();

        return Mono.just(ResponseEntity.ok().body(response));
    }

    @RequestMapping(
            path = "/find-active-categories",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<CategoriesResponse>> findAllActiveCategories(){
        CategoriesResponse response = this.categoryService.findCategoriesByStatus(1);

        return Mono.just(ResponseEntity.ok().body(response));
    }

    @RequestMapping(
            path = "/find-inactive-categories",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<CategoriesResponse>> findAllInactiveCategories(){
        CategoriesResponse response = this.categoryService.findCategoriesByStatus(0);

        return Mono.just(ResponseEntity.ok().body(response));
    }
}
