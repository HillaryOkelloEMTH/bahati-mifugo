package com.emtech.dairyapp.Dairy.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class MifugoConfig {

    @Value("${mifugo.maziwa-token}")
    private String maziwaToken;

    @Bean
    public RestTemplate maziwaRestTemplate() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(45))
                .build();
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build();

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + maziwaToken);
            return execution.execute(request, body);
        }));

        return restTemplate;
    }

}