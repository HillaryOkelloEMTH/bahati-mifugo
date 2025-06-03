package com.emtech.dairyapp.Dairy.Supply.quality;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TestResDto {
    private List<TestDto> tests;
    public static class TestDto {
        @JsonProperty("_id")
        private String _id;
        @JsonProperty("agentNumber")
        private String agentNumber;
        @JsonProperty("farmerNumber")
        private String farmerNumber;
        @JsonProperty("quality")
        private String quality;
        @JsonProperty("litres")
        private Double litres;
        @JsonProperty("pricePerLitre")
        private Double pricePerLitre;
        @JsonProperty("totalAmount")
        private Double totalAmount;
        @JsonProperty("paid")
        private Boolean paid;
        @JsonProperty("createdAt")
        private Date createdAt;
        @JsonProperty("updatedAt")
        private Date updatedAt;
        @JsonProperty("gps")
        private Gps gps;
    }

    public static class Gps {
        @JsonProperty("type")
        private String type;
        @JsonProperty("coordinates")
        private List<String> coordinates;
    }
}
