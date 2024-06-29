package com.emtech.dairyapp.Stock.Data.Http.Response.Product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductData implements Serializable {
    @Builder.Default
    private Long id = null;

    @Builder.Default
    private String name = null;

    @Builder.Default
    private String description = null;

    @Builder.Default
    private Double price = null;

    @Builder.Default
    private Double salePrice = null;

    @Builder.Default
    private Double profit = null;
    @Builder.Default
    private String type = null;
    @Builder.Default
    private Integer stock=null;

    private String category;

    @Builder.Default
    private  Double discount = null;

    @Builder.Default
    private  Integer discounted = null;

    @Builder.Default
    private  Integer deleted = null;

    @Builder.Default
    private Timestamp updateDate = null;

    @Builder.Default
    private Timestamp creationDate = null;
}
