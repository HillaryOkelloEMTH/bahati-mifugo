package com.emtech.dairyapp.Dairy.Supply.bulkuploads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BulkDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer farmerNo;
    private Double quantity;
    private Date date;
    private Date postedOn = new Date();
    private String session;
    private String reason;
    private String postedBy;
    private String farmer;
    private String route;
}
