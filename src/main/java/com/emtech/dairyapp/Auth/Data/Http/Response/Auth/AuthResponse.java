package com.emtech.dairyapp.Auth.Data.Http.Response.Auth;

import com.emtech.dairyapp.Auth.Data.User.UserRoleData;
import lombok.*;

import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    @Builder.Default
    private Long id = null;

    @Builder.Default
    private String username = null;

    @Builder.Default
    private String mobile = null;

    @Builder.Default
    private String refreshToken = null;

    @Builder.Default
    private List<UserRoleData> roles = null;

    @Builder.Default
    private String token = null;

}
