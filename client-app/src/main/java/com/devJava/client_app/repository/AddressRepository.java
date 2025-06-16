package com.devJava.client_app.repository;

import com.devJava.client_app.domain.address.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    @Query("SELECT a FROM Address a WHERE a.customer.id = :customerId")
    public Page<Address> addressesByCustomer(@Param("customerId") UUID cutomerId, Pageable pageable);
}
