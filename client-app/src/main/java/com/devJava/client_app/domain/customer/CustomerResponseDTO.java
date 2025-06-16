package com.devJava.client_app.domain.customer;

import com.devJava.client_app.domain.address.AddressResponseDTO;
import com.devJava.client_app.domain.phone.PhoneResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CustomerResponseDTO(
    UUID id,
    String name,
    String email,
    String cpf,
    List<PhoneResponseDTO> phones,
    List<AddressResponseDTO> addresses,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 