package com.example.authapp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ProductReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Product product;

    @ManyToOne(optional = false)
    private AppUser author;

    private int rating;

    @Column(length = 1200)
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public AppUser getAuthor() { return author; }
    public void setAuthor(AppUser author) { this.author = author; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
