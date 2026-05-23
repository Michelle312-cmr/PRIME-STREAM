package com.example.authapp.repo;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.StreamingMedia;
import com.example.authapp.model.StreamingWatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StreamingWatchlistRepository extends JpaRepository<StreamingWatchlistItem, Long> {
    List<StreamingWatchlistItem> findByUserOrderByAddedAtDesc(AppUser user);
    Optional<StreamingWatchlistItem> findByUserAndMedia(AppUser user, StreamingMedia media);
    boolean existsByUserAndMedia(AppUser user, StreamingMedia media);
    long countByUser(AppUser user);
}
