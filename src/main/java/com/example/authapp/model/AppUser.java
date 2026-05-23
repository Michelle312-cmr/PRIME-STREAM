package com.example.authapp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class AppUser {

    public enum Role {
        ADMIN,
        SELLER,
        USER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)

    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    private String fullName;
    private String phone;
    private String avatarUrl;
    private boolean primeMember;
    private String primePlan;
    private LocalDateTime primeSubscriptionEndsAt;
    private LocalDateTime lastSubscriptionReminderAt;
    private LocalDateTime lastCatalogAnnouncementAt;

    public AppUser() {
    }

    public AppUser(String username, String email, String passwordHash, Set<Role> roles) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isPrimeMember() {
        return primeMember;
    }

    public void setPrimeMember(boolean primeMember) {
        this.primeMember = primeMember;
    }

    public String getPrimePlan() {
        return primePlan;
    }

    public void setPrimePlan(String primePlan) {
        this.primePlan = primePlan;
    }

    public LocalDateTime getPrimeSubscriptionEndsAt() {
        return primeSubscriptionEndsAt;
    }

    public void setPrimeSubscriptionEndsAt(LocalDateTime primeSubscriptionEndsAt) {
        this.primeSubscriptionEndsAt = primeSubscriptionEndsAt;
    }

    public LocalDateTime getLastSubscriptionReminderAt() {
        return lastSubscriptionReminderAt;
    }

    public void setLastSubscriptionReminderAt(LocalDateTime lastSubscriptionReminderAt) {
        this.lastSubscriptionReminderAt = lastSubscriptionReminderAt;
    }

    public LocalDateTime getLastCatalogAnnouncementAt() {
        return lastCatalogAnnouncementAt;
    }

    public void setLastCatalogAnnouncementAt(LocalDateTime lastCatalogAnnouncementAt) {
        this.lastCatalogAnnouncementAt = lastCatalogAnnouncementAt;
    }
}

