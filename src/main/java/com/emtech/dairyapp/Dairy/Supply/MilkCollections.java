package com.emtech.dairyapp.Dairy.Supply;

import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "collections")
public class MilkCollections {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long member;//farmer id
    private String productType;
    private String event;//buying or selling
    private Double currentPrice;
    private Date collectionDate=new Date();

    //quantity
    private Double quantity=0.0;//litres
    //quality percentage
    private Double proteinContent=0.0;
    private Double fatContent=0.0;
    private Double temperature=0.0;

    private String remarks;
    private String contaminationStatus;//
    private Double amount;
    private Long collectorId;
    private Long pickUpLocation;
    private Long wardFk;

    private  Character status= CONSTANTS.NO;
    private Character paymentStatus=CONSTANTS.NO;



}
