package com.emtech.dairyapp.Transactions.Data.Http.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashPaymentRequest implements Serializable {
    @JsonProperty(value = "amount")
    private Double amount;

    @JsonProperty(value = "mobile")
    private String mobile;

    @JsonProperty(value = "collectorId")
    private Long collectorId;

    @JsonProperty(value = "collectionId")
    private  Long collectionId;
}
