package com.emtech.dairyapp.Configurations.FarmerManagement;

import com.emtech.dairyapp.Configurations.SubCounty.Ward;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Farmer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String middleName;
    @Column(unique = true)
    private String idNumber;
    @Column(unique = true)
    private Integer farmerNo;
    private String mobileNo;
    private String alternativeMobileNo;
    private String memberType;//individual/group

    @OneToOne(cascade = CascadeType.ALL)
    private BankDetails bankDetails;

    private String address;
    private String paymentFreequency;
    private Integer paymentDate;
    private String paymentMode; //mpesa/cash/bank

    private Date createdAt = new Date();
    private Character deletedFlag;
    private Date deletedOn;

    private String location;
    private String subLocation;
    private String village;
    private Long county_fk;
    private Long subcounty_fk;
    private Long wardFk;
    private Integer noOfCows;
    private Long routeFk;
    private String transportMeans;
    private String gender;

}
