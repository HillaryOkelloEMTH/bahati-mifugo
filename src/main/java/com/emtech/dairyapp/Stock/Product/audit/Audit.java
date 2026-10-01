package com.emtech.dairyapp.Stock.Product.audit;

import com.emtech.dairyapp.Auth.Utilities.UserInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "audit_log")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String modelName;
    private String objectId;
    private String username;
    private String machineInfo;
    @Column(length = 1000000)
    private String details;


    @Column(name = "timestamp", nullable = false)
    private ZonedDateTime timestamp;
}
