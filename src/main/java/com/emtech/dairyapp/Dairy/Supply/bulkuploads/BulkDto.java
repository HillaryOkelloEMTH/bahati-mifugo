package com.emtech.dairyapp.Dairy.Supply.bulkuploads;

import lombok.Data;

import java.util.Date;

@Data
public class BulkDto {
    private Integer farmerNo;
    private Double quantity;
    private Date date;
    private String session;
}
