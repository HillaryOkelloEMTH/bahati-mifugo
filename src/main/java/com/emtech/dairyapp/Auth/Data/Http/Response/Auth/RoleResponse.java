package com.emtech.dairyapp.Auth.Data.Http.Response.Auth;

import lombok.*;

import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponse {
    List<RoleData> roleData = null;
}
