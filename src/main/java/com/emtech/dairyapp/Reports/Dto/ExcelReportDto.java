package com.emtech.dairyapp.Reports.Dto;

import lombok.Data;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Data
public class ExcelReportDto {
    private HttpHeaders headers;
    private MediaType mediaType = MediaType.parseMediaType("application/vnd.ms-excel");
    private InputStreamResource resource;
}
