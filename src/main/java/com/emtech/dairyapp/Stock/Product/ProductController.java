package com.emtech.dairyapp.Stock.Product;

import com.emtech.dairyapp.Stock.Data.Http.Request.Product.ProductCreateRequest;
import com.emtech.dairyapp.Stock.Data.Http.Response.Product.ProductResponse;
import com.emtech.dairyapp.Stock.Data.Http.Response.Product.ProductsResponse;
import com.emtech.dairyapp.Stock.Data.Http.Response.StockEntitiesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping(
        path = "/api/v1/products"
)
public class ProductController {
    @Autowired
    private ProductService productService;

    @RequestMapping(
            path = "/add",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<StockEntitiesResponse>> createProduct(@RequestBody ProductCreateRequest body){
        StockEntitiesResponse response = this.productService.createProduct(body.getName(), body.getDescription(), body.getPrice() , body.getType(), body.getSalePrice(), body.getStock(), body.getCategory());

        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()) && response.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR.value()){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

    @RequestMapping(
            path = "/update/{productId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<StockEntitiesResponse>> updateProduct(@PathVariable Long productId, @RequestBody ProductCreateRequest body){
        StockEntitiesResponse response = this.productService.updateProduct(productId, body.getName(), body.getDescription(), body.getPrice(), body.getSalePrice());

        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()) && response.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR.value()){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

    @RequestMapping(
            path = "/move-to-recycle-bin/{productId}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<StockEntitiesResponse>> moveToRecycleBin(@PathVariable Long productId){
        StockEntitiesResponse response = this.productService.updateProductStatus(productId, 1);

        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()) && response.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR.value()){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

    @RequestMapping(
            path = "/restore-from-recycle-bin/{productId}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<StockEntitiesResponse>> restoreFromRecycleBin(@PathVariable Long productId){
        StockEntitiesResponse response = this.productService.updateProductStatus(productId, 0);

        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()) && response.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR.value()){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

    @RequestMapping(
            path = "/{productId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<ProductResponse>> findProductDetails(@PathVariable Long productId){
        ProductResponse response = this.productService.getProductDetails(productId);

        return Mono.just(ResponseEntity.ok().body(response));
    }

    @RequestMapping(
            path = "/all",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<ProductsResponse>> findAllProducts(){
        ProductsResponse response = this.productService.findAllProducts();

        return Mono.just(ResponseEntity.ok().body(response));
    }

    @RequestMapping(
            path = "/deleted-products",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<ProductsResponse>> findDeletedProducts(){
        ProductsResponse response = this.productService.findAllProductsByStatus(1);

        return Mono.just(ResponseEntity.ok().body(response));
    }

    @RequestMapping(
            path = "/active-products",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<ProductsResponse>> findActiveProducts(){
        ProductsResponse response = this.productService.findAllProductsByStatus(0);

        return Mono.just(ResponseEntity.ok().body(response));
    }

    @RequestMapping(
            path = "/{productId}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<StockEntitiesResponse>> deleteProducts(@PathVariable Long productId){
        StockEntitiesResponse response = this.productService.deleteProduct(productId);

        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()) && response.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR.value()){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }
}
