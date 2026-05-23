package com.example.authapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "watch_history")
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id")
    private StreamingProfile profile;

    @ManyToOne(optional = false)
    @JoinColumn(name = "media_id")
    private StreamingMedia media;

    private int progressSeconds;
    private int progressPercent;
    private LocalDateTime lastWatchedAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public StreamingProfile getProfile() {
        return profile;
    }

    public void setProfile(StreamingProfile profile) {
        this.profile = profile;
    }

    public StreamingMedia getMedia() {
        return media;
    }

    public void setMedia(StreamingMedia media) {
        this.media = media;
    }

    public int getProgressSeconds() {
        return progressSeconds;
    }

    public void setProgressSeconds(int progressSeconds) {
        this.progressSeconds = progressSeconds;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    public LocalDateTime getLastWatchedAt() {
        return lastWatchedAt;
    }

    public void setLastWatchedAt(LocalDateTime lastWatchedAt) {
        this.lastWatchedAt = lastWatchedAt;
    }
}
