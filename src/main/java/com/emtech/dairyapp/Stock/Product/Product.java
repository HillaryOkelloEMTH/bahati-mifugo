package com.emtech.dairyapp.Stock.Product;


import com.emtech.dairyapp.Stock.Category.Category;
import com.emtech.dairyapp.Stock.CategoryProduct.CategoryProduct;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;


@ToString
@Data
@EqualsAndHashCode
@DynamicUpdate
@Entity
@Table(name = "product", uniqueConstraints = {
        @UniqueConstraint(name = "id", columnNames = {"id"})
})
public class Product implements Serializable {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    private String category;

    @Column(name = "price")
    private Double price;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "type")
    private String type;

    @Column(name = "sale_price")
    private Double salePrice;

    @Column(name = "profit")
    private Double profit;

    @Column(name = "discount")
    private  Double discount;

    @Column(name = "discounted")
    private  Integer discounted;

    @Column(name = "deleted")
    private  Integer deleted;

    @CreationTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "update_date")
    private Timestamp updateDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "Creation_date")
    private Timestamp creationDate;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<CategoryProduct> categoryProducts;
}
