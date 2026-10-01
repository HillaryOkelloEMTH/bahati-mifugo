package com.emtech.dairyapp.Stock.Data.Http.Response;

import lombok.*;
import org.springframework.http.HttpStatus;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockEntitiesResponse {

    @Builder.Default
    private String message = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
    private Integer statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
}
