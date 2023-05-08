package com.emtech.dairyapp.Reports;


import com.emtech.dairyapp.Analytics.AnalyticsData;
import com.emtech.dairyapp.Configurations.FarmerManagement.Farmer;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocations;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocationsRepo;
import com.emtech.dairyapp.Configurations.Profile.Profile;
import com.emtech.dairyapp.Configurations.Profile.ProfileRepo;
import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.Interface.FarmerCollections;

import com.emtech.dairyapp.Dairy.PaymentComponent.PaymentFileData;
import com.emtech.dairyapp.Dairy.ProductAllocations.FarmerProdAllocattionsRepo;
import com.emtech.dairyapp.Dairy.ProductAllocations.FarmerProducts;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
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


    private final ReportService reportService;
    private final ProfileRepo profileRepo;
    private final MilkCollectionRepo collectionRepo;
    private final FarmerProdAllocattionsRepo allocattionsRepo;
    private final PickUpLocationsRepo pickUpLocationsRepo;
    private final FarmerProdAllocattionsRepo farmerProdAllocattionsRepo;
    private final FarmerRepo farmerRepo;



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

    public ReportController(ReportService reportService, ProfileRepo profileRepo, MilkCollectionRepo collectionRepo, FarmerProdAllocattionsRepo allocattionsRepo, PickUpLocationsRepo pickUpLocationsRepo, FarmerProdAllocattionsRepo farmerProdAllocattionsRepo, FarmerRepo farmerRepo) {
        this.reportService = reportService;
        this.profileRepo = profileRepo;
        this.collectionRepo = collectionRepo;
        this.allocattionsRepo = allocattionsRepo;
        this.pickUpLocationsRepo = pickUpLocationsRepo;
        this.farmerProdAllocattionsRepo = farmerProdAllocattionsRepo;
        this.farmerRepo = farmerRepo;
    }


    @GetMapping("collection")
    public ResponseEntity<?> getReceipt(@RequestParam String collectionCode) {

        try {
            Optional<CollectionsData> record = reportService.fetcCollectionsbyCode(collectionCode);
            log.info("recieving request...");
            if (record.isPresent()) {
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
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + record.get().getFarmer()+"-"+ record.get().getCollection_date()+ "-collections-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record.get());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse();
            response.setMessage(exc.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("farmer/collections")
    public ResponseEntity<?> getFarmerCollections(@RequestParam Integer farmerNo) {
        try {

            List<FarmerCollections> record = reportService.getFarmerCollections(farmerNo);
            if (record.size() > 0) {
                log.info("Data found");

                Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(report_path + "/farmerStatement.jrxml"));

                Profile profile = profileRepo.getProfile();

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("farmerId", farmerNo);
                parameters.put("logo", report_icon);
                parameters.put("location", profile.getLocation());
                parameters.put("company", profile.getCompanyName());
                parameters.put("address", profile.getPhysicalAddress());


                JasperPrint print = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(print);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "statement" + "-collections-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse();
            response.setMessage(exc.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("farmer/statement")
    public ResponseEntity<?> getFarmerStatement(@RequestParam Integer farmerNo, @RequestParam String from, @RequestParam String to) {
        try {

            FarmerDetails record = reportService.getFarmerStatement(farmerNo);
            if (record != null) {
                log.info("Farmer found");

                log.info("Farmer name " + record.getFarmerName());
                log.info("Farmer Id " + record.getId());
                Long farmer_id = record.getId();

                Double income = 0.0;
                Double expenses = 0.0;
                Double deliveries = 0.0;
                Double paidincome = 0.0;
                Double paidexpenses = 0.0;
                Double totalPaid = 0.0;

                List<FarmerStmtDetails> fd = collectionRepo.getFarmerStmntdetails(from, to, farmerNo);
//                if (fd.size() > 0) {
//                    log.info("Data found " +fd.size());
                    MilkCollectionRepo.Totals unpaid = collectionRepo.getUnPaidAmount(farmerNo,from,to);
                    MilkCollectionRepo.Totals ut = collectionRepo.getPaidAmount(farmerNo,from,to);
                    income = unpaid.getCollectionAmount();
                    if(income==null){
                        income=0.00;
                    }
                    log.info("income  " + income);

                    deliveries = unpaid.getDeliveries();
                    if(deliveries==null){
                        deliveries=0.00;
                    }
                    log.info("deliveries  " + deliveries);
                    log.info("Farmer id ", farmer_id);

                    expenses = unpaid.getAllocationAmount();
                    if (expenses ==null){
                        expenses=0.00;
                    }
                    log.info("Expenses " + expenses);

                    paidincome=ut.getCollectionAmount();
                    if(paidincome==null){
                        paidincome=0.00;
                    }
                    paidexpenses=ut.getAllocationAmount();
                    if(paidexpenses==null){
                        paidexpenses=0.00;
                    }
                    totalPaid=paidincome+paidexpenses;
                    log.info("Total paid "+totalPaid);




                    Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
                    JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(report_path + "/statement.jrxml"));

                    Profile profile = profileRepo.getProfile();

                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("farmerNo", farmerNo);
                    parameters.put("logo", report_icon);
                    parameters.put("location", profile.getLocation());
                    parameters.put("company", profile.getCompanyName());
                    parameters.put("address", profile.getPhysicalAddress());

                    parameters.put("from", from);
                    parameters.put("to", to);

                    parameters.put("farmername", record.getFarmerName());
                    parameters.put("route", record.getRoute());
                    parameters.put("pickuplocation", record.getPickUpLocation());


                    parameters.put("income", income);
                    parameters.put("deliveries", deliveries);
                    parameters.put("expenses", expenses);



                    JasperPrint print = JasperFillManager.fillReport(compileReport, parameters, connection);
                    byte[] data = JasperExportManager.exportReportToPdf(print);
                    HttpHeaders headers = new HttpHeaders();
                    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "statement" + "-collections-report");
                    return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//                }else {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
//                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                    response.setEntity(null);
//                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//                }
            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse();
            response.setMessage(exc.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("percollectors")
    public ResponseEntity<?> getCollectionsPerCollectors(@RequestParam String date) {
        try {

            List<AnalyticsData> record = reportService.getCollecorColections(date);
            if (record.size() > 0) {
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
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "statement" + "-collections-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse();
            response.setMessage(exc.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("perlocations")
    public ResponseEntity<?> getCollectionsPerLocations(@RequestParam String date) {
        try {

            List<AnalyticsData> record = reportService.getCollectorLocations(date);
            if (record.size() > 0) {
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
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "statement" + "-collections-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse();
            response.setMessage(exc.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("date")
    public ResponseEntity<?> getCollectionsPerDate(@RequestParam String date) {
        try {

            List<ReportData> record = reportService.getDayCollections(date);
            if (record.size() > 0) {
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
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "statement" + "-collections-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse();
            response.setMessage(exc.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("paymentfile")
    public ResponseEntity<?> getCollectionsPerDate(@RequestParam String month,@RequestParam String paymentMode) {
        log.info("Generating payment file...");
        try {

            List<PaymentFileData> record = collectionRepo.getPaymentFileData(month,paymentMode);
            if (record.size() > 0) {
                log.info("Data found ");
                log.info("Data size "+ record.size());

                Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(report_path + "/paymentFile.jrxml"));

                Profile profile = profileRepo.getProfile();

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("month", month);
                parameters.put("mode", paymentMode);
                parameters.put("logo", report_icon);
                parameters.put("location", profile.getLocation());
                parameters.put("company", profile.getCompanyName());
                parameters.put("address", profile.getPhysicalAddress());


                JasperPrint print = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(print);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + month + "-paymentfile-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse();
            response.setMessage(exc.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("weekly/paymentfile")
    public ResponseEntity<?> getweekLyPayment(@RequestParam String week,@RequestParam String paymentMode) {
        log.info("Generating payment file...");
        try {

            List<PaymentFileData> record = collectionRepo.getPaymentFileData(week,paymentMode);
            if (record.size() > 0) {
                log.info("Data found ");
                log.info("Data size "+ record.size());

                Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(report_path + "/paymentFile.jrxml"));

                Profile profile = profileRepo.getProfile();

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("month", month);
                parameters.put("mode", paymentMode);
                parameters.put("logo", report_icon);
                parameters.put("location", profile.getLocation());
                parameters.put("company", profile.getCompanyName());
                parameters.put("address", profile.getPhysicalAddress());


                JasperPrint print = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(print);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + month + "-paymentfile-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse();
            response.setMessage(exc.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("collections/per/pickUpLocation")
    public ResponseEntity<?> getCollectionsPerDate(@RequestParam Long pickUpLocationId, @RequestParam String date) {
        try {

            List<CollectionsData> record = reportService.fetchCollectionsPickUpCollectionsAndDate(pickUpLocationId,date);
            if (record.size() > 0) {
                log.info("Data found");
                log.info("Records found :"+ record.size());
                Optional<PickUpLocations> p =pickUpLocationsRepo.findById(pickUpLocationId);
                String pickupLocation=p.get().getName();


                Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(report_path + "/collectionsPerPickupLocations.jrxml"));

                Profile profile = profileRepo.getProfile();
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("pickupLocation",pickupLocation );
                parameters.put("locationid",pickUpLocationId );
                parameters.put("date",date);
                parameters.put("logo", report_icon);
                parameters.put("location", profile.getLocation());
                parameters.put("company", profile.getCompanyName());
                parameters.put("address", profile.getPhysicalAddress());


                JasperPrint print = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(print);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + pickupLocation + "-collections-report");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("No record found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(record);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse();
            response.setMessage(exc.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("farmer/product/allocations")
    public ResponseEntity<?> getFarmerProducts(@RequestParam Integer farmer_no, @RequestParam String month) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<FarmerInfo> f =farmerRepo.findByFarmerNo(farmer_no);
            if(f.isPresent()) {
                List<FarmerProducts> record = reportService.getFarmerProducts(farmer_no, month);
                if (record.size() > 0) {
                    log.info("Data found");



                    String farmer = f.get().getUsername();
                    Double paidAmount= farmerProdAllocattionsRepo.getFPAmount(farmer_no, CONSTANTS.YES,month);
                    Double UnpPidAmount= farmerProdAllocattionsRepo.getFPAmount(farmer_no, CONSTANTS.NO,month);


                    Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
                    JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(report_path + "/farmerProductAllocations.jrxml"));

                    Profile profile = profileRepo.getProfile();
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("farmer", farmer);
                    parameters.put("farmer_no", farmer_no);
                    parameters.put("month", month);
                    parameters.put("logo", report_icon);
                    parameters.put("location", profile.getLocation());
                    parameters.put("company", profile.getCompanyName());
                    parameters.put("address", profile.getPhysicalAddress());
                    parameters.put("paid", paidAmount);
                    parameters.put("unpaid", UnpPidAmount);



                    JasperPrint print = JasperFillManager.fillReport(compileReport, parameters, connection);
                    byte[] data = JasperExportManager.exportReportToPdf(print);
                    HttpHeaders headers = new HttpHeaders();
                    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + farmer + "-allocationsList-report");
                    return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

                } else {

                    response.setMessage("No record found");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(record);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }else {
                response.setMessage("Farmer with farmer number "+ farmer_no+"  Not found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse();
            response.setMessage(exc.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
