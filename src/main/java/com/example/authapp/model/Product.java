package com.example.authapp.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private BigDecimal oldPrice;
    private String imageUrl;
    private String badge;
    private int stock;
    private double rating;
    private int reviewCount;
    private int soldCount;
    private boolean primeEligible;
    private boolean flashDeal;

    @Column(length = 2000)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> gallery = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> specs = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private AppUser seller;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getOldPrice() { return oldPrice; }
    public void setOldPrice(BigDecimal oldPrice) { this.oldPrice = oldPrice; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public int getSoldCount() { return soldCount; }
    public void setSoldCount(int soldCount) { this.soldCount = soldCount; }
    public boolean isPrimeEligible() { return primeEligible; }
    public void setPrimeEligible(boolean primeEligible) { this.primeEligible = primeEligible; }
    public boolean isFlashDeal() { return flashDeal; }
    public void setFlashDeal(boolean flashDeal) { this.flashDeal = flashDeal; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getGallery() { return gallery; }
    public void setGallery(List<String> gallery) { this.gallery = gallery; }
    public List<String> getSpecs() { return specs; }
    public void setSpecs(List<String> specs) { this.specs = specs; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public AppUser getSeller() { return seller; }
    public void setSeller(AppUser seller) { this.seller = seller; }
}
