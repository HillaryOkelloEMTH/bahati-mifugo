package com.emtech.dairyapp.Reports;


import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("api/v1/reports")
public class ReportController {



    private final  ReportService reportService;


    @Value("${dairy.company_logo_path}")
    private String report_icon;
    @Value("${dairy.report-path}")
    private String report_path;


    @Value("${spring.datasource.url}")
    private String db;
    @Value("${spring.datasource.username}")
    private String dbusername;
    @Value("${spring.datasource.password}")
    private String dbpassword;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }


    @GetMapping("collection")
    public ResponseEntity<?> fetchAllUsersActionItems(@RequestParam String collectionCode){
        try {

            Optional<CollectionsData> record = reportService.fetcCollectionsbyCode(collectionCode);
            if (record.isPresent()){
                log.info("Data found");

                Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(report_path + "/action_items_user_all.jrxml"));

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("code", collectionCode);
                parameters.put("report_icon", report_icon);

                JasperPrint print = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(print);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+record.get().getFarmer()+"-collections-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record.get());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }catch (Exception exc){
            EntityResponse response = new EntityResponse();
            response.setMessage(exc.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
