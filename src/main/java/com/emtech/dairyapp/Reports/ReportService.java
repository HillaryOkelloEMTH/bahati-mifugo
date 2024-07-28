package com.emtech.dairyapp.Reports;

import com.emtech.dairyapp.Analytics.AnalyticsData;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocations;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocationsRepo;
import com.emtech.dairyapp.Configurations.Profile.Profile;
import com.emtech.dairyapp.Configurations.Profile.ProfileRepo;
import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.Interface.FarmerCollections;

import com.emtech.dairyapp.Dairy.ProductAllocations.FarmerProdAllocattionsRepo;
import com.emtech.dairyapp.Dairy.ProductAllocations.FarmerProducts;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;

import com.emtech.dairyapp.Reports.Dto.MccAllocationDto;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Month;
import java.util.*;

@Service
@Slf4j
public class ReportService {

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




    private final MilkCollectionRepo collectionRepo;
    private final FarmerProdAllocattionsRepo allocattionsRepo;

    private final PickUpLocationsRepo pickUpLocationsRepo;

    private final ProfileRepo profileRepo;


    public ReportService(MilkCollectionRepo collectionRepo, FarmerProdAllocattionsRepo allocattionsRepo, PickUpLocationsRepo pickUpLocationsRepo, ProfileRepo profileRepo) {
        this.collectionRepo = collectionRepo;
        this.allocattionsRepo = allocattionsRepo;
        this.pickUpLocationsRepo = pickUpLocationsRepo;
        this.profileRepo = profileRepo;
    }


    public Optional<CollectionsData> fetcCollectionsbyCode(String collectioncode) {
        try {
            return collectionRepo.getCollectionsbyCollectionCode(collectioncode);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public List<CollectionsData> fetchCollectionsPickUpCollectionsAndDate(Long pickupcollations,String date) {
        try {
            return collectionRepo.getCollectionsbyPickUpLocationAndDate(pickupcollations, date);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public List<FarmerCollections> getFarmerCollections(Integer farmerNo) {
        try {

            return collectionRepo.getFarmerCollections(farmerNo);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public FarmerDetails getFarmerStatement(Integer farmerNo) {
        try {
            FarmerDetails f=null;
            Optional<FarmerDetails> farmerDetails=collectionRepo.getFarmerStatementDetails(farmerNo);
            if(farmerDetails.isPresent()){
                 f= farmerDetails.get();
            }
            return f;
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public List<AnalyticsData> getCollecorColections(String date) {
        try {

            return collectionRepo.getCollectorsPerCollector(date);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public List<AnalyticsData> getCollectorLocations(String date) {
        try {

            return collectionRepo.getCollectorsPerLocation(date);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public List<AnalyticsData> getCollectorperMccandDate(String date) {
        try {

            return collectionRepo.getCollectorsPerMCCandDate(date);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public List<AnalyticsData> getCollectorperMccandmonth(String month) {
        try {

            return collectionRepo.getCollectorsPerMCCandmonth(month);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public List<ReportData> getDayCollections(String date) {
        try {

            return collectionRepo.getCollectorsPerDate(date);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public List<FarmerProducts> getFarmerProducts(Integer farmerNo,String month) {
        try {

            return allocattionsRepo.getFarmerProduct(farmerNo, month);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }

    public EntityResponse<MccAllocationDto> getMccAllocations(Long locationId, Integer month, String year) {
        EntityResponse<MccAllocationDto> response = new EntityResponse<>();
        Map<String, Object> params = new HashMap<>(getCompanyProfile());

        try {
            Optional<PickUpLocations> optionalMcc = pickUpLocationsRepo.findById(locationId);

            if (optionalMcc.isEmpty()) {
                response.setMessage("Center with id "+locationId+" not found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return response;
            }

            String monthName = String.valueOf(Month.of(month));

            Integer allocationCount = allocattionsRepo.getMccAllocationsCount(locationId, monthName, year);

            if (allocationCount < 1) {
                response.setMessage("Found 0 allocations for "+optionalMcc.get().getName());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return response;
            }

            String mccName = optionalMcc.get().getName();

            // set parameters
            params.put("locationId", locationId);
            params.put("month", monthName);
            params.put("year", year);
            params.put("mcc", mccName);

            // connect to datasource
            Connection connection = DriverManager.getConnection(this.db, this.dbusername, this.dbpassword);
            JasperReport report = JasperCompileManager.compileReport(new FileInputStream(report_path+"/mcc_allocations.jrxml"));

            // fill report with allocations data
            JasperPrint print = JasperFillManager.fillReport(report, params, connection);
            byte[] data = JasperExportManager.exportReportToPdf(print);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+mccName+"-"+monthName);

            MccAllocationDto body = new MccAllocationDto();
            body.setHeaders(headers);
            body.setData(data);


            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("report retrieved successfully");
            response.setEntity(body);
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("Bad request");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    private Map<String, String> getCompanyProfile() {
        Map<String, String> parameters = new HashMap<>();
        try {
            Profile profileInfo = profileRepo.getProfile();

            parameters.put("logo", report_icon);
            parameters.put("location", profileInfo.getLocation());
            parameters.put("company", profileInfo.getCompanyName());
            parameters.put("address", profileInfo.getPhysicalAddress());
        } catch (Exception e) {
            log.error(e.toString());
        }
        return parameters;
    }

}
