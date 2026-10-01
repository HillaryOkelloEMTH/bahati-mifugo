package com.emtech.dairyapp.Auth.Data.Role;

import com.emtech.dairyapp.Auth.Role.RoleService;
import lombok.*;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleAccessRights {
    @Builder.Default
    private String name = null;

    @Builder.Default
    private RoleService.AccessRight accessRights = null;

}
