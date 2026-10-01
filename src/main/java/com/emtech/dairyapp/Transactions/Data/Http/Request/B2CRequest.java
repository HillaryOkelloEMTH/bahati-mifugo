package com.emtech.dairyapp.Transactions.Data.Http.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class B2CRequest {
    @JsonProperty(value = "amount")
    private Double amount;

    @JsonProperty(value = "phoneNumber")
    private String phoneNumber;
}
