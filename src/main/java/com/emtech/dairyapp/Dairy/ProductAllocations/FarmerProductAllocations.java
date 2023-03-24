package com.emtech.dairyapp.Dairy.ProductAllocations;


import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
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
public class FarmerProductAllocations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long farmerId;
    private Long productId;
    private Date allocatioDate;
    private String allocatedBY;
    private Double quantity;
    private Double amount;
    private Double productPrice;
    private Character revokeStatus=CONSTANTS.NO;
    private Character paymentStatus= CONSTANTS.NO;








}
