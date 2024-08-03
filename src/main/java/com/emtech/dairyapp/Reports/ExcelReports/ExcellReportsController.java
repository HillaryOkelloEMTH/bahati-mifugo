package com.emtech.dairyapp.Reports.ExcelReports;

import lombok.extern.slf4j.Slf4j;
import org.jfree.ui.InsetsTextField;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Month;

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
        String filename = "collections_"+date+".xlsx";
        InputStreamResource file =new InputStreamResource(exelReportService.collecionsPerDate(date));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @GetMapping("payroll/{month}/{year}")
    public ResponseEntity<?> getFarmerPayroll(@PathVariable Integer month, @PathVariable String year) {
        Month m = Month.of(month);
        String mname = m.toString();
        String filename = "payroll-"+mname+"-"+year+".xlsx";
        var response = exelReportService.farmerPayroll(month, year);

        if (response.getStatusCode() == 200) {
            InputStreamResource file = new InputStreamResource(response.getEntity());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+filename)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(file);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }
    }

    @GetMapping("/route-summary-center/{date}/{centerId}")
    public ResponseEntity<Resource> routeSummaryForCenter(@PathVariable String date, @PathVariable Long centerId) {
        String filename = "route_summary_"+date+".xlsx";
        InputStreamResource file =new InputStreamResource(exelReportService.routeSummaryForCenter(date, centerId));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @GetMapping("/route-summary-center/monthly/{month}/{centerId}")
    public ResponseEntity<?> getMccMonthlyRouteSummary(@PathVariable Integer month, @PathVariable Long centerId) {
        Month m = Month.of(month);
        String monthName = m.toString();
        String filename = "mcc_summary_"+monthName+".xlsx";
        var response  = exelReportService.getMccMonthlyRouteSummary(month, centerId);

        if (response.getStatusCode() == 200) {
            InputStreamResource file =new InputStreamResource(response.getEntity());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(file);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }
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
    @GetMapping("/collections/paymentfile/dates")
    public ResponseEntity<Resource> collectinsPerPickuplocations(@RequestParam String from,@RequestParam String to,@RequestParam String mode) {
        String filename = "paymentFile"+mode+".xlsx";
        InputStreamResource file =new InputStreamResource(exelReportService.getPaymentFileDr(from, to, mode));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
    @GetMapping("/collections/paymentfile")
    public ResponseEntity<Resource> collectinsPerPickuplocations(@RequestParam Long pid,@RequestParam String month,@RequestParam String mode) {
        String filename = "paymentFile"+mode+".xlsx";
        InputStreamResource file =new InputStreamResource(exelReportService.getPaymentFile(pid,month, mode));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
}
