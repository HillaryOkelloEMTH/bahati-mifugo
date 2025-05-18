package com.emtech.dairyapp.Dairy.FloatTracking;

import com.emtech.dairyapp.Configurations.SubCounty.Ward;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FloatManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long collectorId;//collector id
    private Double floatAmount=0.00; //100000
    private Double floatSpent=0.0; //100000
    private Double balance=0.0;//9000
    private Date date;
    private String allocatedBy;
    private String mode;
    private Character deletedFlag='N';

//    @OneToMany(targetEntity = FloatDetails.class,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//    @JoinColumn(name = "float_manager_id",referencedColumnName = "id")
//    private List<FloatDetails> details;




}
