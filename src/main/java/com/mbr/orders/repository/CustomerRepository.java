package com.mbr.orders.repository;

import com.mbr.orders.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByIdAndState(Long id, Customer.CustomerState state); // seperate enum later
}
