package com.emtech.dairyapp.Reports;


import com.emtech.dairyapp.Analytics.AnalyticsData;
import com.emtech.dairyapp.Configurations.Profile.Profile;
import com.emtech.dairyapp.Configurations.Profile.ProfileRepo;
import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.Interface.Statement;
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
    private final ProfileRepo profileRepo;


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

    public ReportController(ReportService reportService, ProfileRepo profileRepo) {
        this.reportService = reportService;
        this.profileRepo = profileRepo;
    }


    @GetMapping("collection")
    public ResponseEntity<?> fetchAllUsersActionItems(@RequestParam String collectionCode){
        try {

            Optional<CollectionsData> record = reportService.fetcCollectionsbyCode(collectionCode);
            if (record.isPresent()){
                log.info("Data found");

                Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(report_path + "/collection.jrxml"));

                Profile profile = profileRepo.getProfile();

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("collection_code", collectionCode);
                parameters.put("logo", report_icon);
                parameters.put("location", profile.getLocation());
                parameters.put("company", profile.getCompanyName());
                parameters.put("address", profile.getPhysicalAddress());


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
    @GetMapping("farmer/statement")
    public ResponseEntity<?> getFarmerStatement(@RequestParam Long farmerid){
        try {

            List<Statement> record = reportService.getFarmerStatement(farmerid);
            if (record.size()>0){
                log.info("Data found");

                Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(report_path + "/farmerStatement.jrxml"));

                Profile profile = profileRepo.getProfile();

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("farmerId", farmerid);
                parameters.put("logo", report_icon);
                parameters.put("location", profile.getLocation());
                parameters.put("company", profile.getCompanyName());
                parameters.put("address", profile.getPhysicalAddress());


                JasperPrint print = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(print);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+"statement"+"-collections-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record);
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
    @GetMapping("percollectors")
    public ResponseEntity<?> getCollectionsPerCollectors(@RequestParam String date){
        try {

            List<AnalyticsData> record = reportService.getCollecorColections(date);
            if (record.size()>0){
                log.info("Data found");

                Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(report_path + "/collectionPerCollector.jrxml"));

                Profile profile = profileRepo.getProfile();

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("date", date);
                parameters.put("logo", report_icon);
                parameters.put("location", profile.getLocation());
                parameters.put("company", profile.getCompanyName());
                parameters.put("address", profile.getPhysicalAddress());


                JasperPrint print = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(print);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+"statement"+"-collections-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record);
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
    @GetMapping("perlocations")
    public ResponseEntity<?> getCollectionsPerLocations(@RequestParam String date){
        try {

            List<AnalyticsData> record = reportService.getCollectorLocations(date);
            if (record.size()>0){
                log.info("Data found");

                Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(report_path + "/collectionsPerLocation.jrxml"));

                Profile profile = profileRepo.getProfile();

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("date", date);
                parameters.put("logo", report_icon);
                parameters.put("location", profile.getLocation());
                parameters.put("company", profile.getCompanyName());
                parameters.put("address", profile.getPhysicalAddress());


                JasperPrint print = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(print);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+"statement"+"-collections-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record);
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
    @GetMapping("date")
    public ResponseEntity<?> getCollectionsPerDate(@RequestParam String date){
        try {

            List<ReportData> record = reportService.getDayCollections(date);
            if (record.size()>0){
                log.info("Data found");

                Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(report_path + "/dairycollections.jrxml"));

                Profile profile = profileRepo.getProfile();

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("date", date);
                parameters.put("logo", report_icon);
                parameters.put("location", profile.getLocation());
                parameters.put("company", profile.getCompanyName());
                parameters.put("address", profile.getPhysicalAddress());


                JasperPrint print = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(print);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+"statement"+"-collections-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record);
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
