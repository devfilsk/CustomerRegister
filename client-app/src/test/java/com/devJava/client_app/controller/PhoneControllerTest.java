package com.devJava.client_app.controller;

import com.devJava.client_app.domain.customer.Customer;
import com.devJava.client_app.domain.phone.PhoneRequestDTO;
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
class PhoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private PhoneRequestDTO validPhoneDTO;
    private Customer customer;

    @BeforeEach
    void setUp() {
        validPhoneDTO = new PhoneRequestDTO("62986447722");

        customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setCpf("12345678909");
        customer = customerRepository.save(customer);
    }

    @Test
    void create_WithValidData_ShouldReturnCreatedPhone() throws Exception {
        mockMvc.perform(post("/api/v1/phones/" + customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPhoneDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.number").value(validPhoneDTO.number()))
                .andExpect(jsonPath("$.customerId").value(customer.getId().toString()));
    }

    @Test
    void create_WithNonExistentCustomer_ShouldReturnNotFound() throws Exception {
        UUID nonExistentCustomerId = UUID.randomUUID();
        mockMvc.perform(post("/api/v1/phones/" + nonExistentCustomerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPhoneDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithDuplicatePhoneNumber_ShouldReturnBadRequest() throws Exception {
        // First phone
        mockMvc.perform(post("/api/v1/phones/" + customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPhoneDTO)));

        // Try to create second phone with same number
        mockMvc.perform(post("/api/v1/phones/" + customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPhoneDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_MultiplePhonesForSameCustomer_ShouldSucceed() throws Exception {
        // First phone
        mockMvc.perform(post("/api/v1/phones/" + customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPhoneDTO)))
                .andExpect(status().isOk());

        // Second phone
        PhoneRequestDTO secondPhoneDTO = new PhoneRequestDTO("62986447723");
        mockMvc.perform(post("/api/v1/phones/" + customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondPhoneDTO)))
                .andExpect(status().isOk());
    }
} 