package com.emtech.dairyapp.Transactions.Payment.PaymentOptions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_categories")
public class PaymentCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., Bank, Sacco, Mobile Money

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<PaymentOption> paymentOptions;

    @Column(name="deleted",nullable =false)
        private boolean deleted=false;
    private boolean active=true;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDate createdAt;

    // Getters and Setters
}

