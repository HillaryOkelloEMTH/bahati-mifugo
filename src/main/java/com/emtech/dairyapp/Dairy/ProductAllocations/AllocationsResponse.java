package com.emtech.dairyapp.Dairy.ProductAllocations;

import com.emtech.dairyapp.Dairy.Interface.Allocations;
import com.emtech.dairyapp.Stock.Data.Http.Response.Product.ProductData;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllocationsResponse implements Serializable {
    @Builder.Default
    private Integer statusCode = HttpStatus.NOT_FOUND.value();

    private String message;

    @Builder.Default
    private List<Allocations> entity = null;
}
