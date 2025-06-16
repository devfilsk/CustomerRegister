package com.devJava.client_app.domain.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequestDTO(
        @NotBlank @Size(min = 10) String name,
        @NotBlank @Size(min = 5) String email,
        @NotBlank @Pattern(regexp = "\\d{11}") String cpf
) {
}
