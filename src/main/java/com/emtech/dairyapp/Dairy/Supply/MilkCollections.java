package com.emtech.dairyapp.Dairy.Supply;

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
@Table(name = "collections")
public class MilkCollections {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long member;
    private Date collectionDate;
    private Double morningLitres;
    private Double noonLitres;
    private Double eveningLitres;
    private String collectorName;








}
