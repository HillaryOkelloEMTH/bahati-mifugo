package com.emtech.dairyapp.Auth.Data.Http.Request.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "password")
    private String password;
}
