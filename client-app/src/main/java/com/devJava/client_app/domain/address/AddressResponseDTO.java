package com.devJava.client_app.domain.address;

import com.devJava.client_app.domain.customer.Customer;

import java.time.LocalDateTime;
import java.util.UUID;

public record AddressResponseDTO(
        UUID id,
        String name,
        String street,
        String number,
        String postcode,
        String city,
        String uf,
        UUID customerId,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {
}
