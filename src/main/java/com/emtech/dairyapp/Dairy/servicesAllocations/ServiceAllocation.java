package com.emtech.dairyapp.Dairy.servicesAllocations;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ServiceAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long serviceId;
    private Date heatStartDate;
    private Integer numberofCows;
    private String description;
    private String requestedBy;
    private String servicingStatus; //Pending, Cancelled, Done
    private Date requestedOn;
    private Date updatedOn;
    private String updatedBy;
    private Integer farmerno;
    private Double amount;
    private String paymentStatus; //N or Y
}
