package com.example.authapp.repo;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(String name, String category, Pageable pageable);
    List<Product> findTop6ByOrderBySoldCountDesc();
    List<Product> findTop6ByOrderByCreatedAtDesc();
    List<Product> findTop6ByFlashDealTrueOrderBySoldCountDesc();
    List<Product> findTop4ByCategoryAndIdNotOrderBySoldCountDesc(String category, Long id);
    List<Product> findBySellerOrderByCreatedAtDesc(AppUser seller);
}
