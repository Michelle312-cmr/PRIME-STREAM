package com.example.authapp.repo;

import com.example.authapp.model.Product;
import com.example.authapp.model.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findByProductOrderByCreatedAtDesc(Product product);
}
