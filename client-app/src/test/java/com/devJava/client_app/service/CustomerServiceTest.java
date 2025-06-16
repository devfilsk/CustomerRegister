package com.devJava.client_app.service;

import com.devJava.client_app.domain.customer.Customer;
import com.devJava.client_app.domain.customer.CustomerRequestDTO;
import com.devJava.client_app.domain.customer.CustomerResponseDTO;
import com.devJava.client_app.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomerServiceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CpfValidatorService cpfValidatorService;

    @Autowired
    private CustomerService customerService;

    private CustomerRequestDTO validCustomerDTO;
    private Customer customer;

    @BeforeEach
    void setUp() {
        validCustomerDTO = new CustomerRequestDTO(
            "John Doe Silva",
            "john@example.com",
            "33632750050"
        );

        customer = new Customer();
        customer.setName(validCustomerDTO.name());
        customer.setEmail(validCustomerDTO.email());
        customer.setCpf(validCustomerDTO.cpf());
    }

    @Test
    void store_WithValidData_ShouldCreateCustomer() {
        CustomerResponseDTO result = customerService.store(validCustomerDTO);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(validCustomerDTO.name(), result.name());
        assertEquals(validCustomerDTO.email(), result.email());
        assertEquals(validCustomerDTO.cpf(), result.cpf());
        
        Customer savedCustomer = customerRepository.findById(result.id()).orElse(null);
        assertNotNull(savedCustomer);
        assertEquals(result.id(), savedCustomer.getId());
    }

    @Test
    void store_WithInvalidCPF_ShouldThrowException() {
        CustomerRequestDTO invalidDTO = new CustomerRequestDTO(
            validCustomerDTO.name(),
            validCustomerDTO.email(),
            "123" // Invalid CPF
        );

        assertThrows(IllegalArgumentException.class, () -> 
            customerService.store(invalidDTO)
        );
    }

    @Test
    void store_WithExistingCPF_ShouldThrowException() {
        // First customer
        customerService.store(validCustomerDTO);

        // Try to create second customer with same CPF
        assertThrows(IllegalArgumentException.class, () -> 
            customerService.store(validCustomerDTO)
        );
    }

    @Test
    void update_WithValidData_ShouldUpdateCustomer() {
        // Create initial customer
        CustomerResponseDTO savedCustomer = customerService.store(validCustomerDTO);
        UUID customerId = savedCustomer.id();

        // Update data
        CustomerRequestDTO updateDTO = new CustomerRequestDTO(
            "Updated Name",
            "updated@example.com",
            validCustomerDTO.cpf()
        );

        CustomerResponseDTO result = customerService.update(customerId, updateDTO);

        assertNotNull(result);
        assertEquals(customerId, result.id());
        assertEquals(updateDTO.name(), result.name());
        assertEquals(updateDTO.email(), result.email());
        assertEquals(updateDTO.cpf(), result.cpf());
    }

    @Test
    void update_WithNonExistentCustomer_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> 
            customerService.update(nonExistentId, validCustomerDTO)
        );
    }

    @Test
    void list_ShouldReturnPaginatedCustomers() {
        // Create multiple customers
        customerService.store(validCustomerDTO);
        customerService.store(new CustomerRequestDTO(
            "Jane Doe",
            "jane@example.com",
            "81590956001"
        ));

        Page<CustomerResponseDTO> result = customerService.list(0, 10);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    void findById_WithExistingCustomer_ShouldReturnCustomer() {
        CustomerResponseDTO savedCustomer = customerService.store(validCustomerDTO);
        UUID customerId = savedCustomer.id();

        CustomerResponseDTO result = customerService.findById(customerId);

        assertNotNull(result);
        assertEquals(customerId, result.id());
        assertEquals(validCustomerDTO.name(), result.name());
    }

    @Test
    void findById_WithNonExistentCustomer_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> 
            customerService.findById(nonExistentId)
        );
    }

    @Test
    void delete_WithExistingCustomer_ShouldDeleteCustomer() {
        CustomerResponseDTO savedCustomer = customerService.store(validCustomerDTO);
        UUID customerId = savedCustomer.id();

        customerService.delete(customerId);

        assertFalse(customerRepository.existsById(customerId));
    }

    @Test
    void delete_WithNonExistentCustomer_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> 
            customerService.delete(nonExistentId)
        );
    }
} 