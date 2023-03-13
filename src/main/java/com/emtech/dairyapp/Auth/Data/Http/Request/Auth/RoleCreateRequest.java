package com.emtech.dairyapp.Auth.Data.Http.Request.Auth;

import com.emtech.dairyapp.Auth.Role.RoleService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleCreateRequest implements Serializable {

    @JsonProperty(value = "role")
    private String name;

    @JsonProperty(value = "privileges")
    private List<RoleService.AccessRight> accessRightList;
}
