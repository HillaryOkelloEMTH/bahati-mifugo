package com.emtech.dairyapp.Transactions.Data.Http.Response;

import com.emtech.dairyapp.Transactions.Data.Payment.PaymentData;
import com.emtech.dairyapp.Transactions.Payment.Payment;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse implements Serializable {
    @Builder.Default
    private Integer statusCode = HttpStatus.NOT_FOUND.value();

    @Builder.Default
    private PaymentData entity = null;
}
