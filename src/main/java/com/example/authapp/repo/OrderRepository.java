package com.example.authapp.repo;

import com.example.authapp.model.AppUser;
import com.example.authapp.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByCustomerOrderByCreatedAtDesc(AppUser customer);
}
