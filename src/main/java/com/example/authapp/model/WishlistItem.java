package com.example.authapp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class WishlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private AppUser user;

    @ManyToOne(optional = false)
    private Product product;

    private int position;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
