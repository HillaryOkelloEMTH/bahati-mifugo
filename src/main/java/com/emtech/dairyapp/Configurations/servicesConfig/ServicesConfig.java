package com.emtech.dairyapp.Configurations.servicesConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "services")
public class ServicesConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String servicename;
    private Double serviceAmount;
    private String status; //Available or Unavailable
    private Long routeFk;
    private Date createdDate;
    private Date modifiedDate;
}
