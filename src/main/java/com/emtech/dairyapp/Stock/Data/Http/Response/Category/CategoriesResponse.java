package com.emtech.dairyapp.Stock.Data.Http.Response.Category;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriesResponse implements Serializable {
    @Builder.Default
    private Integer statusCode = HttpStatus.NOT_FOUND.value();
    @Builder.Default
    List<CategoryData> categoryData = null;
}
