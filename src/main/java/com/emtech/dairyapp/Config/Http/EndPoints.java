package com.emtech.dairyapp.Config.Http;

import java.util.List;

public class EndPoints {
    public static final List<String> allowedUrls = List.of(
            "/swagger-ui/**",
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/api/v1/authentication/**",
            "api/v1/sms-notifications/callback",
            "/bahati-*/**",
            "/api/v1/quality/**"
    );
}