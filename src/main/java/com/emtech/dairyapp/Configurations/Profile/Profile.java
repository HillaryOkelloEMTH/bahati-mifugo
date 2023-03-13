package com.emtech.dairyapp.Configurations.Profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String companyName;
    private String companyEmail;
    private String entity;
    private String regNo;
    private String phone;
    private String location;
    private String physicalAddress;
    private  String website;
    @Lob
    private  String logo;
    private Date createdAt;


}
