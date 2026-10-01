package com.emtech.dairyapp.intergrations.mifugo;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MifugoClient {

    private final RestTemplate maziwaRestTemplate;

    @Value("${mifugo.maziwa-base-url}")
    private String maziwaBaseUrl;

    public MifugoClient(@Qualifier("maziwaRestTemplate") RestTemplate maziwaRestTemplate) {
        this.maziwaRestTemplate = maziwaRestTemplate;
    }

    /**
     * Fetches farmer details + all animals (each with muzzleImage URL already
     * included) in a single call. Replaces the old separate farmer search +
     * animals fetch + per-animal muzzle detail enrichment.
     */
    @Cacheable(value = "maziwaFarmerProfile", key = "#nationalId", unless = "#result == null")
    @CircuitBreaker(name = "maziwaFarmerProfile", fallbackMethod = "farmerProfileFallback")
    public Map<String, Object> getFarmerProfileByNationalId(String nationalId) {
        String url = maziwaBaseUrl + "/farmers/by-national-id/" + nationalId;
        return maziwaRestTemplate.getForObject(url, Map.class);
    }

    @SuppressWarnings("unused")
    private Map<String, Object> farmerProfileFallback(String nationalId, Throwable t) {
        log.warn("Maziwa farmer profile fetch failed/circuit-open for nationalId {}: {}", nationalId, t.getMessage());
        return null;
    }






}