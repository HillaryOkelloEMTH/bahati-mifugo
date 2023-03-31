package com.emtech.dairyapp.Transactions.Data.Payment;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentData implements Serializable {
    @Builder.Default
    private Long id = null;

    @Builder.Default
    private String resultCode = null;

    @Builder.Default
    private String merchantRequestID = null;

    @Builder.Default
    private String resultDescription = null;

    @Builder.Default
    private Double amount = null;

    @Builder.Default
    private String mpesaReceiptNumber = null;

    @Builder.Default
    private Timestamp transactionDate = null;

    @Builder.Default
    private Long phoneNumber = null;

    @Builder.Default
    private String status = null;

    @Builder.Default
    private Timestamp createdDate = null;
}
