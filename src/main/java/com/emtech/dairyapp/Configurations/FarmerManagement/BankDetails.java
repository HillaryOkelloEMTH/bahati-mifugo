package com.emtech.dairyapp.Configurations.FarmerManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.sql.Update;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(updatable = false)
    private Long id;
    private String bankName;
    private String accountName;
    private String accountNumber;
    private String branch;
    private String otherMeans;
    private String otherMeansDetails;


}
