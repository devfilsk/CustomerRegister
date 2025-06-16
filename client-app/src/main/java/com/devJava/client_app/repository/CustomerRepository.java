package com.devJava.client_app.repository;

import com.devJava.client_app.domain.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    Optional<Customer> findByCpf(String cpf);
}
