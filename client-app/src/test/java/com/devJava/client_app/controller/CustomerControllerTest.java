package com.devJava.client_app.controller;

import com.devJava.client_app.domain.customer.Customer;
import com.devJava.client_app.domain.customer.CustomerRequestDTO;
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

import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerRequestDTO validCustomerDTO;
    private Customer customer;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        validCustomerDTO = new CustomerRequestDTO(
            "John Doe Silva",
            "john@example.com",
            "12345678909"
        );

        customer = new Customer();
        customer.setName(validCustomerDTO.name());
        customer.setEmail(validCustomerDTO.email());
        customer.setCpf(validCustomerDTO.cpf());
    }

    @Test
    void create_WithValidData_ShouldReturnCreatedCustomer() throws Exception {
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCustomerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(validCustomerDTO.name()))
                .andExpect(jsonPath("$.email").value(validCustomerDTO.email()))
                .andExpect(jsonPath("$.cpf").value(validCustomerDTO.cpf()));

        // Verify customer was actually saved in database
        Optional<Customer> savedCustomer = customerRepository.findByCpf(validCustomerDTO.cpf());
        assertNotNull(savedCustomer);
        assertEquals(validCustomerDTO.name(), savedCustomer.get().getName());
    }

    @Test
    void update_WithValidData_ShouldReturnUpdatedCustomer() throws Exception {
        // First create a customer
        Customer savedCustomer = customerRepository.save(customer);
        UUID customerId = savedCustomer.getId();

        CustomerRequestDTO updateDTO = new CustomerRequestDTO(
            "Updated Name",
            "updated@example.com",
            validCustomerDTO.cpf()
        );

        mockMvc.perform(put("/api/v1/customers/" + customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value(updateDTO.name()))
                .andExpect(jsonPath("$.email").value(updateDTO.email()))
                .andExpect(jsonPath("$.cpf").value(updateDTO.cpf()));

        // Verify customer was actually updated in database
        Customer updatedCustomer = customerRepository.findById(customerId).orElse(null);
        assertNotNull(updatedCustomer);
        assertEquals(updateDTO.name(), updatedCustomer.getName());
        assertEquals(updateDTO.email(), updatedCustomer.getEmail());
    }

    @Test
    void list_ShouldReturnPaginatedCustomers() throws Exception {
        // Create multiple customers
        customerRepository.save(customer);
        Customer secondCustomer = new Customer();
        secondCustomer.setName("Jane Doe");
        secondCustomer.setEmail("jane@example.com");
        secondCustomer.setCpf("98765432109");
        customerRepository.save(secondCustomer);

        mockMvc.perform(get("/api/v1/customers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[1].name").exists());
    }

    @Test
    void findById_WithExistingCustomer_ShouldReturnCustomer() throws Exception {
        Customer savedCustomer = customerRepository.save(customer);
        UUID customerId = savedCustomer.getId();

        mockMvc.perform(get("/api/v1/customers/" + customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value(validCustomerDTO.name()))
                .andExpect(jsonPath("$.email").value(validCustomerDTO.email()))
                .andExpect(jsonPath("$.cpf").value(validCustomerDTO.cpf()));
    }

    @Test
    void delete_WithExistingCustomer_ShouldReturnNoContent() throws Exception {
        Customer savedCustomer = customerRepository.save(customer);
        UUID customerId = savedCustomer.getId();

        mockMvc.perform(delete("/api/v1/customers/" + customerId))
                .andExpect(status().isNoContent());

        // Verify customer was actually deleted from database
        assertFalse(customerRepository.existsById(customerId));
    }

    @Test
    void create_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CustomerRequestDTO invalidDTO = new CustomerRequestDTO(
            "John", // Name too short
            "invalid-email",
            "123" // Invalid CPF
        );

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        // Verify no customer was saved
        assertTrue(customerRepository.findAll().isEmpty());
    }
} 