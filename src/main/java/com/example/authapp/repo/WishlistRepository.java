package com.example.authapp.repo;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.Product;
import com.example.authapp.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserOrderByPositionAscCreatedAtDesc(AppUser user);
    Optional<WishlistItem> findByUserAndProduct(AppUser user, Product product);
    long countByUser(AppUser user);
}
