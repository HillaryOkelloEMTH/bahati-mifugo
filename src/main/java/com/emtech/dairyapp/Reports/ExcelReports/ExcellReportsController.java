package com.emtech.dairyapp.Reports.ExcelReports;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("api/v1/excel/reports")
public class ExcellReportsController {
    private final ExelReportService exelReportService;

    public ExcellReportsController(ExelReportService exelReportService) {
        this.exelReportService = exelReportService;
    }

    @GetMapping("/collectionsPerDate")
    public ResponseEntity<Resource> downloadExcel(@RequestParam String date) {
        String filename = "collectionsPerDateReport.xlsx";
        InputStreamResource file =new InputStreamResource(exelReportService.collecionsPerDate(date));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
    @GetMapping("/collections/pickuplocation")
    public ResponseEntity<Resource> collectinsPerPickuplocations(@RequestParam Long pid,@RequestParam String date) {
        String filename = "collectionsPerPickuplocations.xlsx";
        InputStreamResource file =new InputStreamResource(exelReportService.getCollectionsPerLocationsexcel(pid, date));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
}
