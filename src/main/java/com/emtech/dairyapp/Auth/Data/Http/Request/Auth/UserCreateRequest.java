package com.emtech.dairyapp.Auth.Data.Http.Request.Auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest implements Serializable {
    @JsonProperty(value = "user_name")
    private String username;

    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    @JsonProperty(value = "email")
    private String email;

    @JsonProperty(value = "mobile")
    private String mobile;

    @JsonProperty(value = "role")
    private Long role;

}
