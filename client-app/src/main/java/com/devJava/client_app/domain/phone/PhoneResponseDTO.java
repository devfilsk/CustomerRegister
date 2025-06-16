package com.devJava.client_app.domain.phone;

import java.time.LocalDateTime;
import java.util.UUID;

public record PhoneResponseDTO(
    UUID id,
    String number,
    UUID customerId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 