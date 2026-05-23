package com.example.authapp.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "streaming_media")
public class StreamingMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String type;

    @Column(length = 1400)
    private String description;

    private int releaseYear;
    private int durationMinutes;
    private String ageRating;
    private String director;
    private double rating;
    private int views;
    private boolean premiumOnly;
    private boolean featured;
    private boolean topTen;
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(length = 900)
    private String posterUrl;

    @Column(length = 900)
    private String bannerUrl;

    @Column(length = 900)
    private String videoUrl;

    @Column(length = 900)
    private String subtitleUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "streaming_media_genres", joinColumns = @JoinColumn(name = "media_id"))
    @Column(name = "genre")
    private Set<String> genres = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "streaming_media_languages", joinColumns = @JoinColumn(name = "media_id"))
    @Column(name = "language")
    private Set<String> languages = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "streaming_media_cast", joinColumns = @JoinColumn(name = "media_id"))
    @Column(name = "cast_member")
    private Set<String> castMembers = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public boolean isPremiumOnly() {
        return premiumOnly;
    }

    public void setPremiumOnly(boolean premiumOnly) {
        this.premiumOnly = premiumOnly;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public boolean isTopTen() {
        return topTen;
    }

    public void setTopTen(boolean topTen) {
        this.topTen = topTen;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getSubtitleUrl() {
        return subtitleUrl;
    }

    public void setSubtitleUrl(String subtitleUrl) {
        this.subtitleUrl = subtitleUrl;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public Set<String> getCastMembers() {
        return castMembers;
    }

    public void setCastMembers(Set<String> castMembers) {
        this.castMembers = castMembers;
    }
}
