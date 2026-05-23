package com.example.authapp.repo;

import com.example.authapp.model.MediaReview;
import com.example.authapp.model.StreamingMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaReviewRepository extends JpaRepository<MediaReview, Long> {
    List<MediaReview> findTop5ByMediaOrderByCreatedAtDesc(StreamingMedia media);
}
