package com.example.authapp.repo;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.AvailabilityRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AvailabilityRequestRepository extends JpaRepository<AvailabilityRequest, Long> {

    @Query("SELECT a FROM AvailabilityRequest a LEFT JOIN FETCH a.matchedMedia WHERE a.user = :user AND a.notified = true ORDER BY a.notifiedAt DESC")
    List<AvailabilityRequest> findTop5ByUserAndNotifiedTrueOrderByNotifiedAtDesc(@Param("user") AppUser user);

    @Query("SELECT a FROM AvailabilityRequest a LEFT JOIN FETCH a.matchedMedia WHERE a.user = :user ORDER BY a.createdAt DESC")
    List<AvailabilityRequest> findTop8ByUserOrderByCreatedAtDesc(@Param("user") AppUser user);

    List<AvailabilityRequest> findByNotifiedFalseOrderByCreatedAtAsc();

    boolean existsByUserAndSearchedTitleIgnoreCaseAndNotifiedFalse(AppUser user, String searchedTitle);
}