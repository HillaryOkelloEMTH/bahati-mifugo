package com.emtech.dairyapp.Reports.ExcelReports;

import com.emtech.dairyapp.Analytics.AnalyticsData;
import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.PaymentComponent.PaymentFileData;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;



import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExelReportService {



    private final MilkCollectionRepo collectionRepo;

    private final MilkCollectionService milkCollectionService;

    public ExelReportService(MilkCollectionRepo collectionRepo, MilkCollectionService milkCollectionService) {
        this.collectionRepo = collectionRepo;
        this.milkCollectionService = milkCollectionService;
    }

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//    static String[] HEADERs = { "Id", "Title", "Description", "Published" };
    static String SHEET = "Collections Records";

    private void createHeaderRow(Row headerRow,String[] headers) {


        for (int col = 0; col < headers.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(headers[col]);
        }
    }
    public ByteArrayInputStream collecionsPerDate(String date) {
        try (Workbook workbook = new XSSFWorkbook();ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);
            String[] headers = { "Farmer","Quantity", "FarmerNo","Collection Date" ,"Collector","Session","Route","Pick-Up Location"};

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

    private void fillDataRow(Row row, CollectionsData entity) {
        row.createCell(0).setCellValue(entity.getFarmer());
        row.createCell(1).setCellValue(entity.getQuantity());
        row.createCell(2).setCellValue(entity.getFarmer_no());
        row.createCell(3).setCellValue(entity.getCollection_date().toString());
        row.createCell(4).setCellValue(entity.getCollector());
        row.createCell(5).setCellValue(entity.getSession());
        row.createCell(6).setCellValue(entity.getRoute());
        row.createCell(7).setCellValue(entity.getPickUpLocation());
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
            String[] headers = { "Farmer","Quantity", "Amount", "DeliveryNumber","Collection Date","Collector","Session" ,"CAN","Route","Pick-Up Location"};

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
