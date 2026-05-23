package com.example.authapp.repo;

import com.example.authapp.model.StreamingMedia;
import com.example.authapp.model.StreamingProfile;
import com.example.authapp.model.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    Optional<WatchHistory> findByProfileAndMedia(StreamingProfile profile, StreamingMedia media);
    List<WatchHistory> findTop12ByProfileOrderByLastWatchedAtDesc(StreamingProfile profile);
}
