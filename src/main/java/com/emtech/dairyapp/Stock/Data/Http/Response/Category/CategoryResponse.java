package com.emtech.dairyapp.Stock.Data.Http.Response.Category;

import lombok.*;
import org.springframework.http.HttpStatus;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    @Builder.Default
    private Integer statusCode = HttpStatus.NOT_FOUND.value();
    @Builder.Default
    private CategoryData categoryData = null;
}
