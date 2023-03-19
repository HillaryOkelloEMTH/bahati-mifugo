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
    @NotBlank
    @Column(unique = true)
    private String username;
    private String firstName;
    private String lastName;
    @NotBlank
    @Column(unique = true)
    private String idNumber;
    @NotBlank @Column(unique = true)
    private String memberCode;
    @NotBlank @Column(precision = 0)
    private String mobileNo;
    private String alternativeMobileNo;
    private String memberType;
    private String address;
    private String paymentFreequency;

    private Date createdAt=new Date();
    private Character deletedFlag;
    private Date deletedOn;
    private String bankAccountNo;
    private Long subcounty_fk;
    private Long wardFk;
    private Integer noOfCows;

}
