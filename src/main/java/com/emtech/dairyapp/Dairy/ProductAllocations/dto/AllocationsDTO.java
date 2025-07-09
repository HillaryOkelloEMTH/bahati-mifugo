package com.emtech.dairyapp.Dairy.ProductAllocations.dto;

import com.emtech.dairyapp.Auth.Utilities.RequestStatus;
import com.emtech.dairyapp.Dairy.Interface.Allocations;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllocationsDTO implements Allocations {

    private Long id;
    private String product;
    private String username;
    private RequestStatus status;
    private String type;
    private Integer noOfCows;
    private Date heatStartDate;
    private Integer farmer_no;
    private Double amount;
    private Integer quantity;
    private Date allocationDate;
    private Date requestedOn;
    private String comments;
    private String time;
    private String allocatedBy;
    private Date approvalDate;
    private Character paymentStatus;
    private Character revokeStatus;


}
