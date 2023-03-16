package com.emtech.dairyapp.Dairy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FloatManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long collectorId;//
    private Long collectionId;
    private Double floatAmount; //100000
    private Double balance;//9000
    private Date date;

}
