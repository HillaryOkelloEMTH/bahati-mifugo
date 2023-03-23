package com.emtech.dairyapp.Stock.CategoryProduct;

import com.emtech.dairyapp.Stock.Category.Category;
import com.emtech.dairyapp.Stock.Product.Product;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryProductRepository extends JpaRepository<CategoryProduct, Long> {
    Optional<CategoryProduct> findByCategoryAndProduct(@NonNull Category c, @NonNull Product p);

    List<CategoryProduct> findAllByCategory(@NonNull Category c);

}
