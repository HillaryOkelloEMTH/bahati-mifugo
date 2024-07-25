package com.emtech.dairyapp.Stock.Product.bulk;

import com.emtech.dairyapp.Auth.User.User;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.ProductPriceConfiguration.ProductConfig;
import com.emtech.dairyapp.Configurations.Utils.Formatter;
import com.emtech.dairyapp.Dairy.Supply.MilkCollections;
import com.emtech.dairyapp.Dairy.Supply.bulkuploads.BulkDto;
import com.emtech.dairyapp.Response.EntityResponse;
import com.emtech.dairyapp.Stock.Product.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class BulkProductService {

    @Autowired
    private ProductService productService;

    public Mono<EntityResponse<?>> uploadBulkProducts(FilePart filePart) {
        EntityResponse<List<Object>> response = new EntityResponse<>();
        List<Object> failed = new ArrayList<>();

        return filePart.content()
                .collectList()
                .flatMap(dataBuffers -> {
                    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        dataBuffers.forEach(dataBuffer -> {
                            byte[] buffer = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(buffer);
                            outputStream.write(buffer, 0, buffer.length);
                        });

                        try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                            var fileUpload = getData(inputStream, filePart.filename());
                            List<BulkProductDto> bulkDtos = fileUpload.getEntity();

                            if (fileUpload.getStatusCode() != 200) {
                                response.setMessage(fileUpload.getMessage());
                                response.setStatusCode(fileUpload.getStatusCode());
                                return Mono.just(response);
                            }

                            for (BulkProductDto row : bulkDtos) {
                                productService.createProduct(row.getName(), row.getDescription(), row.getPrice() , row.getType(), row.getSalePrice(), row.getStock(), row.getCategory(), row.getPriceType());
                            }

                            response.setMessage("Bulk Collections uploaded successfully");
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(failed);
                        } catch (Exception e) {
                            log.error(e.toString());
                            response.setMessage("Bad Request");
                            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        }
                    } catch (IOException e) {
                        log.error(e.toString());
                        response.setMessage("Error reading file");
                        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    }
                    return Mono.just(response);
                });
    }

    public EntityResponse<List<BulkProductDto>> getData(InputStream inputStream, String filename) {
        List<BulkProductDto> bulkDtos = new LinkedList<>();
        EntityResponse<List<BulkProductDto>> response = new EntityResponse<>();

        try {
            Workbook workbook = null;
            if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage("Kindly upload an excel file");
                return response;
            }

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            //check that the correct header rows are being passed
            if (!validHeaders(headerRow)) {
                response.setMessage("Wrong header rows passed");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return response;
            }


            for (Row record : sheet) {
                if (record.getRowNum() == 0) {
                    continue;
                }

                BulkProductDto bulkDto = new BulkProductDto();

                if (record.getCell(0) != null) {
                    var name = record.getCell(0).getStringCellValue();
                    bulkDto.setName(name);
                } else {
                    continue;
                }

                if (record.getCell(1) != null) {
                    String description = record.getCell(1).getStringCellValue();
                    bulkDto.setDescription(description);
                } else {
                    continue;
                }

                if (record.getCell(2) != null) {
                    Double price = record.getCell(2).getNumericCellValue();
                    bulkDto.setPrice(price);
                } else {
                    continue;
                }

                if (record.getCell(3) != null) {
                    Double salePrice = record.getCell(3).getNumericCellValue();
                    bulkDto.setSalePrice(salePrice);
                } else {
                    continue;
                }

                if (record.getCell(4) != null) {
                    var stock = record.getCell(4).getNumericCellValue();
                    bulkDto.setStock((int) stock);
                } else {
                    continue;
                }

                if (record.getCell(5) != null) {
                    String type = record.getCell(5).getStringCellValue();
                    bulkDto.setType(type);
                } else {
                    continue;
                }

                if (record.getCell(6) != null) {
                    var category = record.getCell(6).getNumericCellValue();
                    bulkDto.setCategory((long) category);
                } else {
                    continue;
                }

                if (record.getCell(7) != null) {
                    String priceType = record.getCell(7).getStringCellValue();
                    bulkDto.setPriceType(priceType);
                } else {
                    continue;
                }

                bulkDtos.add(bulkDto);
            }

            if (bulkDtos.isEmpty()) {
                response.setMessage("Data not found");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return response;
            }

            response.setMessage("success");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(bulkDtos);
        } catch (IOException e) {
            log.error(e.toString());
            response.setMessage("Incorrect data format");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }


    private boolean validHeaders(Row headerRow) {
        if (headerRow == null) {
            return false;
        }

        String[] expectedHeaders = {"name", "description", "price", "salePrice", "stock", "type", "category", "priceType"};

        for (int i=0; i<expectedHeaders.length; i++ ) {
            if (headerRow.getCell(i) == null || !expectedHeaders[i].equalsIgnoreCase(headerRow.getCell(i).getStringCellValue())) {
                return false;
            }
        }
        return true;
    }
}
