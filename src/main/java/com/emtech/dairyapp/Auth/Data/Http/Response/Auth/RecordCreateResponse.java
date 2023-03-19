package com.emtech.dairyapp.Auth.Data.Http.Response.Auth;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecordCreateResponse implements Serializable {
    @Builder.Default
    private String message = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();

    @Builder.Default
    private Integer statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
}
