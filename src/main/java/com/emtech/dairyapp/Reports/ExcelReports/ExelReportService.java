package com.emtech.dairyapp.Reports.ExcelReports;

import com.emtech.dairyapp.Analytics.AnalyticsData;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Dairy.Interface.AllocationDataInterface;
import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.PaymentComponent.PaymentFileData;
import com.emtech.dairyapp.Dairy.ProductAllocations.FarmerProdAllocattionsRepo;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionService;
import com.emtech.dairyapp.Reports.Dto.PayrollInterface;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import javax.swing.text.html.parser.Entity;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExelReportService {



    private final MilkCollectionRepo collectionRepo;

    private final MilkCollectionService milkCollectionService;

    private final FarmerRepo farmerRepo;

    private final FarmerProdAllocattionsRepo allocattionsRepo;

    public ExelReportService(MilkCollectionRepo collectionRepo, MilkCollectionService milkCollectionService, FarmerRepo farmerRepo, FarmerProdAllocattionsRepo allocattionsRepo) {
        this.collectionRepo = collectionRepo;
        this.milkCollectionService = milkCollectionService;
        this.farmerRepo = farmerRepo;
        this.allocattionsRepo = allocattionsRepo;
    }

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//    static String[] HEADERs = { "Id", "Title", "Description", "Published" };
    static String SHEET = "Collections Records";

    private void createHeaderRow(Row headerRow,String[] headers) {
        // header cell style
        Workbook workbook = headerRow.getSheet().getWorkbook();
        CellStyle headerStyle = workbook.createCellStyle();

        // font weight and size for header cells
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);

        headerStyle.setFont(font);

        // align to center
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        for (int col = 0; col < headers.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(headers[col]);
            cell.setCellStyle(headerStyle);
        }
    }
    public ByteArrayInputStream collecionsPerDate(String date) {
        try (Workbook workbook = new XSSFWorkbook();ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);
            String[] headers = { "Farmer No", "Farmer", "Quantity", "Collection Date" , "Session", "Route", "Pickup Location"};

            List<CollectionsData> data = collectionRepo.getCollectionsbyDate(date); // Fetch data from the database

            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            createHeaderRow(headerRow,headers); // Create header row

            for (CollectionsData entity : data) {
                Row row = sheet.createRow(rowNum++);
                fillDataRow(row, entity); // Fill data rows
            }
                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public EntityResponse<ByteArrayInputStream> farmerPayroll(Integer month, String year) {
        EntityResponse<ByteArrayInputStream> response = new EntityResponse<>();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();){
            Sheet sheet = workbook.createSheet(SHEET);
            String[] headers = {"Farmer", "Farmer No", "Mobile No", "Quantity", "Price", "Income", "Expenses", "NetPay", "Bank", "Account No", "Branch", "Route", "Mcc"};
            List<PayrollInterface> data = farmerRepo.getFarmerPayroll(month, year);

            // header row
            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            createHeaderRow(headerRow, headers);

            for (PayrollInterface record: data) {
                Row row = sheet.createRow(rowNum++);
                fillPayroll(row, record);
            }
            workbook.write(out);

            response.setMessage("retrieved payroll successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(new ByteArrayInputStream(out.toByteArray()));
        } catch (IOException e) {
            log.error(e.toString());
            response.setMessage("Unable to generate payroll");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    public ByteArrayInputStream routeSummaryForCenter(String date, Long centerId) {
        try (Workbook workbook = new XSSFWorkbook();ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);
            String[] headers = { "Route","Quantity", "Session 1","Session 2" ,"Session 3"};

            List<AnalyticsData> data = milkCollectionService.getRouteSummaryForCenter(date, centerId); // Fetch data from the database
            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            createHeaderRow(headerRow,headers); // Create header row

            Map<String, Map<String, Double>> sessionData = new HashMap<>();
            Map<String, AnalyticsData> routeSummary = new HashMap<>();
            Map<String, String> collectors = new HashMap<>();


            for (AnalyticsData entity: data) {
                String route = entity.getRoute();
                String session = entity.getSession();
                Double quantity = entity.getQuantity();

                sessionData.putIfAbsent(route, new HashMap<>());
                sessionData.get(route).put(session, quantity);

                if (!sessionData.containsKey(route)) {
                    routeSummary.put(route, entity);
                }
                // Collect the first encountered collector for each route
                if (!collectors.containsKey(route)) {
                    collectors.put(route, entity.getCollector());
                }
            }

            for (String route : sessionData.keySet()) {
                Row row = sheet.createRow(rowNum++);
                Map<String, Double> quantities = sessionData.get(route);
                AnalyticsData entity = routeSummary.get(route);
                String collector = collectors.get(route);

                row.createCell(0).setCellValue(route);
                row.createCell(1).setCellValue(quantities.values().stream().mapToDouble(Double::doubleValue).sum());
                row.createCell(2).setCellValue(quantities.getOrDefault("Session 1", 0.0));
                row.createCell(3).setCellValue(quantities.getOrDefault("Session 2", 0.0));
                row.createCell(4).setCellValue(quantities.getOrDefault("Session 3", 0.0));
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }

    }

    public EntityResponse<ByteArrayInputStream> getRouteDeliverySummary(Long routeId, int month, String year) {
        EntityResponse<ByteArrayInputStream> response = new EntityResponse<>();

        try(Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            List<MilkCollectionRepo.RouteTotals> summary = collectionRepo.getRouteSummary(routeId, month, year);

            Sheet sheet = workbook.createSheet(SHEET);
            String[] headers = {"Date", "Quantity"};

            // header row
            int rowNo = 0;
            Row headerRow = sheet.createRow(rowNo++);
            createHeaderRow(headerRow, headers);

            for (MilkCollectionRepo.RouteTotals data: summary) {
                Row row = sheet.createRow(rowNo++);

                row.createCell(0).setCellValue(data.getDate());
                row.createCell(1).setCellValue(data.getQuantity());
            }

            workbook.write(out);

            response.setMessage("Route summary report retrieved successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("An error occurred");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    public EntityResponse<ByteArrayInputStream> getMccMonthlyRouteSummary(Integer month, Long centerId) {
        EntityResponse<ByteArrayInputStream> response = new EntityResponse<>();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);
            String[] headers = {"Route", "Quantity", "Date", "Amount"};
            List<AnalyticsData> data = milkCollectionService.getMccMonthlyRouteSummary(month, centerId);

            // header row
            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            createHeaderRow(headerRow, headers);

            for (AnalyticsData record: data) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(record.getRoute());
                row.createCell(1).setCellValue(record.getQuantity());
                row.createCell(2).setCellValue(record.getDate());
                row.createCell(3).setCellValue(record.getAmount());
            }
            workbook.write(out);

            response.setMessage("retrieved summary successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(new ByteArrayInputStream(out.toByteArray()));

        } catch (IOException e) {
            log.error(e.toString());
            response.setMessage("Unable to generate summary");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    public EntityResponse<ByteArrayInputStream> getAllocationsHistory(Integer month, String year) {
        EntityResponse<ByteArrayInputStream> response = new EntityResponse<>();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            List<AllocationDataInterface> allocations = allocattionsRepo.getAllocationsByPeriod(month, year);

            Sheet sheet = workbook.createSheet(SHEET);
            String[] headers = {"farmerno", "farmer", "requestedon", "approvedon", "product", "quantity", "amount", "status", "mcc"};

            //createing the header row
            int rowNo = 0;
            Row headerRow = sheet.createRow(rowNo++);
            createHeaderRow(headerRow, headers);

            for (AllocationDataInterface data: allocations) {
                Row row = sheet.createRow(rowNo++);
                fillAllocationsReport(row, data);
            }

            workbook.write(out);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Allocations report generated successfully");
            response.setEntity(new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception e) {
            log.error(e.toString());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("An error occurred");
        }
        return response;
    }




    private void fillDataRow(Row row, CollectionsData entity) {
        row.createCell(0).setCellValue(entity.getFarmer_no());
        row.createCell(1).setCellValue(entity.getFarmer());
        row.createCell(2).setCellValue(entity.getQuantity());
        row.createCell(3).setCellValue(entity.getCollection_date().toString());
        row.createCell(4).setCellValue(entity.getSession());
        row.createCell(5).setCellValue(entity.getRoute());
        row.createCell(6).setCellValue(entity.getPickUpLocation());
    }

    private void fillPayroll(Row row, PayrollInterface pd) {
        row.createCell(0).setCellValue(pd.getFarmer());
        row.createCell(1).setCellValue(pd.getFno());
        row.createCell(2).setCellValue(pd.getMobileNo());
        row.createCell(3).setCellValue(pd.getQty());
        row.createCell(4).setCellValue(pd.getPrice());
        row.createCell(5).setCellValue(pd.getIncome());
        row.createCell(6).setCellValue(pd.getExpenses());
        row.createCell(7).setCellValue(pd.getNetpay());
        row.createCell(8).setCellValue(pd.getBname());
        row.createCell(9).setCellValue(pd.getAccno());
        row.createCell(10).setCellValue(pd.getBranch());
        row.createCell(11).setCellValue(pd.getRoute());
        row.createCell(12).setCellValue(pd.getMcc());
    }

    private void fillAllocationsReport(Row row, AllocationDataInterface data) {
        row.createCell(0).setCellValue(data.getFarmer_no());
        row.createCell(1).setCellValue(data.getFarmer());
        row.createCell(2).setCellValue(data.getRequested_on());
        row.createCell(3).setCellValue(data.getApproval_date());
        row.createCell(4).setCellValue(data.getProduct());
        row.createCell(5).setCellValue(data.getQuantity());
        row.createCell(6).setCellValue(data.getAmount());
        row.createCell(7).setCellValue(data.getStatus().toString());
        row.createCell(8).setCellValue(data.getMcc());
    }
    private void fillDataRowPaymentFile(Row row, PaymentFileData entity) {
        row.createCell(0).setCellValue(entity.getFarmer_no());
        row.createCell(1).setCellValue(entity.getUsername());
        row.createCell(2).setCellValue(entity.getMobile_no());
        row.createCell(3).setCellValue(entity.getCollectionAmount());
        row.createCell(4).setCellValue(entity.getAllocationAmount());
        row.createCell(5).setCellValue(entity.getNetPay());
        row.createCell(6).setCellValue(entity.getPayment_mode());
        row.createCell(7).setCellValue(entity.getAccount_name());
        row.createCell(8).setCellValue(entity.getAccount_number());
        row.createCell(9).setCellValue(entity.getBranch());
    }

    public ByteArrayInputStream getCollectionsPerLocationsexcel(Long pid,String date) {
        try (Workbook workbook = new XSSFWorkbook();ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);
            String[] headers = { "Farmer No", "Farmer","Quantity", "Collection Date", "Session" ,"Route", "Pickup Location"};

            List<CollectionsData> data = collectionRepo.getCollectionsbyPickUpLocationAndDate(pid,date); // Fetch data from the database

            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            createHeaderRow(headerRow,headers); // Create header row

            for (CollectionsData entity : data) {
                Row row = sheet.createRow(rowNum++);
                fillDataRow(row, entity); // Fill data rows
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
    public ByteArrayInputStream getPaymentFileDr(String from,String to,String mode) {
        try (Workbook workbook = new XSSFWorkbook();ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);
            List<PaymentFileData> data=null;
            String[] headers = { "FarmerNo","Farmer", "Mobile Number", "CollectionAmount","Expenses","NetPay","PaymentMode" ,"AccountName","Account Number","Branch"};
            if(mode.equalsIgnoreCase("M-Pesa") || mode.equalsIgnoreCase("Cash")) {
                data = collectionRepo.getPaymentFileDataMpesaDateRange(from, to, mode); // Fetch data from the database
            }else {
             data = collectionRepo.getPaymentFileDataModeDateRange(from, to, mode); // Fetch data from the database
            }
            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            createHeaderRow(headerRow,headers); // Create header row

            for (PaymentFileData entity : data) {
                Row row = sheet.createRow(rowNum++);
                fillDataRowPaymentFile(row, entity); // Fill data rows
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
    public ByteArrayInputStream getPaymentFile(Long pid,String month,String mode) {
        try (Workbook workbook = new XSSFWorkbook();ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);
            List<PaymentFileData> data=null;
            String[] headers = { "FarmerNo","Farmer", "Mobile Number", "CollectionAmount","Expenses","NetPay","PaymentMode" ,"AccountName","Account Number","Branch"};
            if(mode.equalsIgnoreCase("M-Pesa") || mode.equalsIgnoreCase("Cash")) {
                data = collectionRepo.getPaymentFileData(pid, month, mode); // Fetch data from the database
            }else {
                data = collectionRepo.getPaymentFileDataB(pid, month, mode); // Fetch data from the database
            }

            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            createHeaderRow(headerRow,headers); // Create header row

            for (PaymentFileData entity : data) {
                Row row = sheet.createRow(rowNum++);
                fillDataRowPaymentFile(row, entity); // Fill data rows
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

}
