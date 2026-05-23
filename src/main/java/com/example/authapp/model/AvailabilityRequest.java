package com.example.authapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "availability_requests")
public class AvailabilityRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(nullable = false)
    private String searchedTitle;

    private String preferredGenre;
    private boolean notified;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime notifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_media_id")
    private StreamingMedia matchedMedia;

    public Long getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getSearchedTitle() {
        return searchedTitle;
    }

    public void setSearchedTitle(String searchedTitle) {
        this.searchedTitle = searchedTitle;
    }

    public String getPreferredGenre() {
        return preferredGenre;
    }

    public void setPreferredGenre(String preferredGenre) {
        this.preferredGenre = preferredGenre;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getNotifiedAt() {
        return notifiedAt;
    }

    public void setNotifiedAt(LocalDateTime notifiedAt) {
        this.notifiedAt = notifiedAt;
    }

    public StreamingMedia getMatchedMedia() {
        return matchedMedia;
    }

    public void setMatchedMedia(StreamingMedia matchedMedia) {
        this.matchedMedia = matchedMedia;
    }
}
