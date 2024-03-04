package com.emtech.dairyapp.Reports.ExcelReports;

import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.PaymentComponent.PaymentFileData;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;



import java.io.*;
import java.util.List;

@Service
public class ExelReportService {



    private final MilkCollectionRepo collectionRepo;

    public ExelReportService(MilkCollectionRepo collectionRepo) {
        this.collectionRepo = collectionRepo;
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
            String[] headers = { "Farmer","Quantity", "Amount", "DeliveryNumber","Collection Date" ,"Collector","Session","CAN","Route","Pick-Up Location"};

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

    private void fillDataRow(Row row, CollectionsData entity) {
        row.createCell(0).setCellValue(entity.getFarmer());
        row.createCell(1).setCellValue(entity.getQuantity());
        row.createCell(2).setCellValue(entity.getAmount());
        row.createCell(3).setCellValue(entity.getCollectionCode());
        row.createCell(4).setCellValue(entity.getCollection_date().toString());
        row.createCell(5).setCellValue(entity.getCollector());
        row.createCell(6).setCellValue(entity.getSession());
        row.createCell(7).setCellValue(entity.getCanNo());
        row.createCell(8).setCellValue(entity.getRoute());
        row.createCell(9).setCellValue(entity.getPickUpLocation());


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
