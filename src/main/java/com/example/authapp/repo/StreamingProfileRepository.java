package com.example.authapp.repo;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.StreamingProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StreamingProfileRepository extends JpaRepository<StreamingProfile, Long> {
    List<StreamingProfile> findByUserOrderByIdAsc(AppUser user);
    Optional<StreamingProfile> findFirstByUserOrderByIdAsc(AppUser user);
}
