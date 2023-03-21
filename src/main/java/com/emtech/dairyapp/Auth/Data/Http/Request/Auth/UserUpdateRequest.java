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
public class UserUpdateRequest {
    @JsonProperty(value = "firstName")
    private String firstName;

    @JsonProperty(value = "lastName")
    private String lastName;
}
