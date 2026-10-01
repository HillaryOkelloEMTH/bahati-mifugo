package com.emtech.dairyapp.Transactions.Mpesa;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@ToString
@Data
@EqualsAndHashCode(of = {"id"})
@DynamicUpdate
@Entity
@Table(name = "mpesa_b2c_transaction", uniqueConstraints = {
        @UniqueConstraint(name = "mpesa_b2c_transaction_id", columnNames = {"id"}),
        @UniqueConstraint(name = "mpesa_b2c_transaction_conversation_id", columnNames = {"conversation_id"}),
        @UniqueConstraint(name = "mpesa_b2c_transaction_originator_conversation_id", columnNames = {"originator_conversation_id"})
})
public class MpesaB2CTransaction implements Serializable {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id")
    private String conversationId;

    @Column(name = "originator_conversation_id")
    private String originatorConversationId;

    @Column(name = "response_code")
    private Integer responseCode;

    @Column(name = "response_description")
    private String responseDescription;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;
    @CreationTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "create_date", nullable = false)
    private Timestamp createdDate;
}
