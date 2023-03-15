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
    @NotBlank
    private Long member;
    private String idNumber;
    private Date collectionDate=new Date();
    //quantity
    private Double quantity=0.0;//litres
    //quality percentage
    private Double poteinContent=0.0;
    private Double fatContent=0.0;
    private Double temperature=0.0;

    private String remarks;
    private String contaminationStatus;




//    private Double morningLitres;
//    private Double noonLitres;
//    private Double eveningLitres;
    private String collectorName;
    private Long pickUpLocation;
    private Long wardFk;
    private  Character status= CONSTANTS.NO;











}
