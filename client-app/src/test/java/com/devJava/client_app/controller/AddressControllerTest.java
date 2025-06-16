package com.devJava.client_app.controller;

import com.devJava.client_app.domain.address.AddressRequestDTO;
import com.devJava.client_app.domain.customer.Customer;
import com.devJava.client_app.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private AddressRequestDTO validAddressDTO;
    private Customer customer;

    @BeforeEach
    void setUp() {
        validAddressDTO = new AddressRequestDTO(
            "Home",
            "Rua das Flores",
            "123",
            "12345-678",
            "São Paulo",
            "SP"
        );

        customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setCpf("12345678909");
        customer = customerRepository.save(customer);
    }

    @Test
    void create_WithValidData_ShouldReturnCreatedAddress() throws Exception {
        mockMvc.perform(post("/api/v1/addresses/" + customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddressDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(validAddressDTO.name()))
                .andExpect(jsonPath("$.street").value(validAddressDTO.street()))
                .andExpect(jsonPath("$.number").value(validAddressDTO.number()))
                .andExpect(jsonPath("$.postcode").value(validAddressDTO.postcode()))
                .andExpect(jsonPath("$.city").value(validAddressDTO.city()))
                .andExpect(jsonPath("$.uf").value(validAddressDTO.uf()));
    }

    @Test
    void create_WithNonExistentCustomer_ShouldReturnNotFound() throws Exception {
        UUID nonExistentCustomerId = UUID.randomUUID();
        mockMvc.perform(post("/api/v1/addresses/" + nonExistentCustomerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddressDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void listAddresses_ShouldReturnPaginatedAddresses() throws Exception {
        // Create multiple addresses
        mockMvc.perform(post("/api/v1/addresses/" + customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddressDTO)));

        mockMvc.perform(post("/api/v1/addresses/" + customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AddressRequestDTO(
                    "Work",
                    "Avenida Paulista",
                    "1000",
                    "01310-100",
                    "São Paulo",
                    "SP"
                ))));

        mockMvc.perform(get("/api/v1/addresses")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    void listCustomerAddresses_ShouldReturnCustomerAddresses() throws Exception {
        // Create addresses for the customer
        mockMvc.perform(post("/api/v1/addresses/" + customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddressDTO)));

        mockMvc.perform(post("/api/v1/addresses/" + customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AddressRequestDTO(
                    "Work",
                    "Avenida Paulista",
                    "1000",
                    "01310-100",
                    "São Paulo",
                    "SP"
                ))));

        mockMvc.perform(get("/api/v1/addresses/" + customer.getId() + "/all")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }
} 