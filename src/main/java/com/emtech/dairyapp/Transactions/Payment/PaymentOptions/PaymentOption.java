package com.emtech.dairyapp.Transactions.Payment.PaymentOptions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_options", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class PaymentOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_category_id", nullable = false)
    private PaymentCategory category;

    @Column(nullable = false,unique=true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean active = true;

    @Column(name="deleted" ,nullable=false)
    private boolean deleted=false;

    // Getters and Setters
}

