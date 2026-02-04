package com.emtech.dairyapp.Reports.ExcelReports;

import lombok.extern.slf4j.Slf4j;
import org.jfree.ui.InsetsTextField;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("location/date-range")
    private ResponseEntity<?> centerDeliveryPerDateRange(@RequestParam Integer lid, @RequestParam String from, String to) {
        var res = exelReportService.centerDeliveryPerDateRange(lid, from, to);
        return ResponseEntity.status(res.getStatusCode()).contentType(res.getEntity().getMediaType())
                .headers(res.getEntity().getHeaders()).body(res.getEntity().getResource());
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



    @GetMapping("/payroll/date-range")
    public ResponseEntity<?> getFarmerPayrollByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        var response = exelReportService.farmerPayrollByDateRange(startDate, endDate);

        if (response.getStatusCode() == 200) {
            InputStreamResource file = new InputStreamResource(response.getEntity());
            String filename = "payroll_" + startDate + "_to_" + endDate + ".xlsx";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(file);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response.getMessage());
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

    @GetMapping("allocations/history/{month}/{year}")
    public ResponseEntity<?> getAllocationsHistory(@PathVariable Integer month, @PathVariable String year) {
        var response = exelReportService.getAllocationsHistory(month, year);
        Month m = Month.of(month);
        String monthName = m.toString();
        String filename = "allocation_history_"+monthName+".xlsx";

        if (response.getStatusCode() == 200) {
            InputStreamResource file = new InputStreamResource(response.getEntity());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+filename).contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }
    }

    @GetMapping("route/summary/{routeId}/{month}/{year}")
    public ResponseEntity<?> getRouteDeliverySummary(@PathVariable Long routeId, @PathVariable int month, @PathVariable String year) {
        var response = exelReportService.getRouteDeliverySummary(routeId, month, year);
        Month monthValue = Month.of(month);
        String monthName = monthValue.toString();
        String fileName = "summary_"+monthName+".xlsx";

        if (response.getStatusCode() == 200) {
            InputStreamResource file = new InputStreamResource(response.getEntity());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName).contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
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
