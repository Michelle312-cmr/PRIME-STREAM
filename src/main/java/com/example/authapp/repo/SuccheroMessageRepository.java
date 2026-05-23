package com.example.authapp.repo;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.SuccheroMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuccheroMessageRepository extends JpaRepository<SuccheroMessage, Long> {
    List<SuccheroMessage> findTop12ByUserOrderByCreatedAtDesc(AppUser user);
}
