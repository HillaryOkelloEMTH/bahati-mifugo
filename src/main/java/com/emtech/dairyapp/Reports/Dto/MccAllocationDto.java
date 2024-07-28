package com.emtech.dairyapp.Reports.Dto;

import lombok.Data;
import org.springframework.http.HttpHeaders;

@Data
public class MccAllocationDto {
    private HttpHeaders headers;
    private byte[] data;
}
