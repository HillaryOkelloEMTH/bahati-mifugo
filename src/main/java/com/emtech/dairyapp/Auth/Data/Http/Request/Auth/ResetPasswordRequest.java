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
public class ResetPasswordRequest {
    @JsonProperty(value = "resetPasswordToken")
    private String resetPasswordToken;

    @JsonProperty(value = "password")
    private String password;
}
