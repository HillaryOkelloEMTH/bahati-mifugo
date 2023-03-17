package com.emtech.dairyapp.Auth.Data.Http.Request.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {
    @JsonProperty(value = "username")
    private String username;
}
