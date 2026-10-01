//package com.emtech.dairyapp.Reports.ExcelReports;
//
//
//import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
//import com.emtech.dairyapp.Dairy.Supply.deliveries.MilkCollectionRepo;
//import com.opencsv.CSVWriter;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.handler.codec.http.*;
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class ExcelExporterService  {
//
//
//
//    private final MilkCollectionRepo collectionRepo;
//
//
//    public ExcelExporterService(MilkCollectionRepo milkCollectionRepo) {
//        this.collectionRepo = milkCollectionRepo;
//
//    }
//
//    public String generateCSVFile(String filePath,String date) throws IOException {
//        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
//            List<CollectionsData> data = collectionRepo.getCollectionsbyDate(date); // Fetch data from the database
//
//            StringBuilder csvBuilder = new StringBuilder();
//            csvBuilder.append("Column 1,Column 2\n"); // CSV header row
//
//            for (CollectionsData entity : data) {
//                csvBuilder.append(entity.getFarmer()).append(",").append(entity.getQuantity()).append("\n"); // CSV data rows
//            }
//
//            return csvBuilder.toString();
//        }
//
//    }
//    public void downloadCSVFile(ChannelHandlerContext ctx,String filePath,String date) {
//        File file = new File(filePath);
//        try (InputStream inputStream = new FileInputStream(file)) {
//
//            String csvData = generateCSVFile(filePath,date);
//
//            ByteBuf content = Unpooled.copiedBuffer(csvData, StandardCharsets.UTF_8);
//            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
//
//            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/csv");
//            response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment; filename=data.csv");
//            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
//
//            ctx.writeAndFlush(response);
////            response.flushBuffer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//}
