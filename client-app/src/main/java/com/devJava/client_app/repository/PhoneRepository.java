package com.devJava.client_app.repository;

import com.devJava.client_app.domain.phone.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PhoneRepository extends JpaRepository<Phone, UUID> {
}
