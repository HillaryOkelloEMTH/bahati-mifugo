package com.emtech.dairyapp.Stock.CategoryProduct;

import com.emtech.dairyapp.Stock.Category.Category;
import com.emtech.dairyapp.Stock.Product.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@ToString
@Data
@EqualsAndHashCode(of = {"id"})
@DynamicUpdate
@Entity
public class CategoryProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

//    @ManyToOne(optional = false)
//    @JoinColumn(name = "category",
//            referencedColumnName = "id",
//            nullable = false,
//            foreignKey = @ForeignKey(name = "category_product_category"))
//    private Category category;
//
//    @ManyToOne(optional = false)
//    @JoinColumn(name = "product",
//            referencedColumnName = "id",
//            nullable = false,
//            foreignKey = @ForeignKey(name = "category_product_product"))
//    private Product product;


    @CreationTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "creation_date", nullable = false)
    private Timestamp creationDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "update_date", nullable = false)
    private Timestamp updateDate;
}
