package com.emtech.dairyapp.Dairy.Supply.deliveries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.Date;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "supply")
public class MilkSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String member;
    private Date recordDate;
    private Double totalQuantity;
    private Double totalAmount;
    private Double amountPaid;
    private Double balance;


}
