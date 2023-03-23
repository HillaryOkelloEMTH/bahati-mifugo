package com.emtech.dairyapp.Stock.Data.Http.Response.Product;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse implements Serializable {
    @Builder.Default
    private Integer statusCode = HttpStatus.NOT_FOUND.value();

    @Builder.Default
    private ProductData productData = null;
}
