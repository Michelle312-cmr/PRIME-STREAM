package com.example.authapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "streaming_profiles")
public class StreamingProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String avatarUrl;
    private String maturityLevel = "18+";
    private String preferredLanguage = "Francais";
    private String preferredQuality = "Auto";
    private String moodMode = "classic";
    private String preferredGenre = "";

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMaturityLevel() {
        return maturityLevel;
    }

    public void setMaturityLevel(String maturityLevel) {
        this.maturityLevel = maturityLevel;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getPreferredQuality() {
        return preferredQuality;
    }

    public void setPreferredQuality(String preferredQuality) {
        this.preferredQuality = preferredQuality;
    }

    public String getMoodMode() {
        return moodMode;
    }

    public void setMoodMode(String moodMode) {
        this.moodMode = moodMode;
    }

    public String getPreferredGenre() {
        return preferredGenre;
    }

    public void setPreferredGenre(String preferredGenre) {
        this.preferredGenre = preferredGenre;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }
}
