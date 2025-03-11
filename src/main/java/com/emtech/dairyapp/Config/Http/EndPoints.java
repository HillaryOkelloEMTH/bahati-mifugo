package com.emtech.dairyapp.Config.Http;

import java.util.List;

public class EndPoints {
    public static final List<String> allowedUrls = List.of(
            "/swagger-*/**",
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/api/v1/authentication/**"
    );
}