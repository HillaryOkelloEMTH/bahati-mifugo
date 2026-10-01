package com.emtech.dairyapp.Transactions.Data.Http.Response;

import com.emtech.dairyapp.Transactions.Data.Payment.PaymentData;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentsResponse implements Serializable {
    @Builder.Default
    private Integer statusCode = HttpStatus.NOT_FOUND.value();

    @Builder.Default
    private List<PaymentData> entity = null;
}
