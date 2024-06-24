package com.emtech.dairyapp.Stock.Data.Http.Response.Product;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductsResponse implements Serializable {
    @Builder.Default
    private Integer statusCode = HttpStatus.NOT_FOUND.value();

    private String message;

    @Builder.Default
    private List<ProductData> productData = null;

}
