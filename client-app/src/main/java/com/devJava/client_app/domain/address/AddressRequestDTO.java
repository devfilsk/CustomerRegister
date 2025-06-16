package com.devJava.client_app.domain.address;

public record AddressRequestDTO(
        String name,
        String street,
        String number,
        String postcode,
        String city,
        String uf
) {
}
