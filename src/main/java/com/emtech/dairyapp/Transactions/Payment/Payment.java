package com.emtech.dairyapp.Transactions.Payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import java.sql.Timestamp;

@ToString
@Data
@EqualsAndHashCode(of = {"id"})
@DynamicUpdate
@Entity
@Table(name = "payments", uniqueConstraints = {
        @UniqueConstraint(name = "payment_id", columnNames = {"id"}),
        @UniqueConstraint(name = "mpesa_stk_transaction_merchant_request_id", columnNames = {"merchant_request_id"})
})
public class Payment {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "result_code")
    private String resultCode;

    @Column(name = "merchant_request_id")
    private String merchantRequestID;

    @Column(name = "result_description")
    private String resultDescription;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "transaction_date")
    private Timestamp transactionDate;

    @Column(name = "phone_number")
    private Long phoneNumber;

    @Column(name = "status")
    private String status;

    @CreationTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "create_date", nullable = false)
    private Timestamp createdDate;
}
