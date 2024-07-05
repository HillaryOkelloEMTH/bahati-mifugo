package com.emtech.dairyapp.Dairy.Supply.returns;

import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "returns")
public class MilkReturns {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer farmerNo;//farmer no
    @Column(unique = true)
    private String collectionNumber;
    private String canNo;
    private String productType;
    private String event;//buying or selling
    private Date collectionDate;
    private Double originalQuantity=0.0;//KG
    private Double quantity=0.0;//KG
    private Double deductedWeight=0.0;
    private String session;
    private Character returned= CONSTANTS.YES;
    private String remarks;
    @Column(nullable = false)
    private Long collectorId;
    private Long routeFk;
    private  Date returnedOn= new Date();
}