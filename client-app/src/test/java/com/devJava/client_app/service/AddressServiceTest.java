package com.devJava.client_app.service;

import com.devJava.client_app.domain.address.Address;
import com.devJava.client_app.domain.address.AddressRequestDTO;
import com.devJava.client_app.domain.address.AddressResponseDTO;
import com.devJava.client_app.domain.customer.Customer;
import com.devJava.client_app.repository.AddressRepository;
import com.devJava.client_app.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AddressServiceTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressService addressService;

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
    void store_WithValidData_ShouldCreateAddress() {
        AddressResponseDTO result = addressService.store(customer.getId(), validAddressDTO);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(validAddressDTO.name(), result.name());
        assertEquals(validAddressDTO.street(), result.street());
        assertEquals(validAddressDTO.number(), result.number());
        assertEquals(validAddressDTO.postcode(), result.postcode());
        assertEquals(validAddressDTO.city(), result.city());
        assertEquals(validAddressDTO.uf(), result.uf());

        Address savedAddress = addressRepository.findById(result.id()).orElse(null);
        assertNotNull(savedAddress);
        assertEquals(result.id(), savedAddress.getId());
        assertEquals(validAddressDTO.name(), savedAddress.getName());
        assertEquals(validAddressDTO.street(), savedAddress.getStreet());
        assertEquals(validAddressDTO.number(), savedAddress.getNumber());
        assertEquals(validAddressDTO.postcode(), savedAddress.getPostcode());
        assertEquals(validAddressDTO.city(), savedAddress.getCity());
        assertEquals(validAddressDTO.uf(), savedAddress.getUf());
        assertEquals(customer.getId(), savedAddress.getCustomer().getId());
    }

    @Test
    void store_WithNonExistentCustomer_ShouldThrowException() {
        UUID nonExistentCustomerId = UUID.randomUUID();
        assertThrows(RuntimeException.class, () -> 
            addressService.store(nonExistentCustomerId, validAddressDTO)
        );
    }

    @Test
    void getAddresses_ShouldReturnPaginatedAddresses() {
        // Create multiple addresses
        AddressResponseDTO firstAddress = addressService.store(customer.getId(), validAddressDTO);
        AddressResponseDTO secondAddress = addressService.store(customer.getId(), new AddressRequestDTO(
            "Work",
            "Avenida Paulista",
            "1000",
            "01310-100",
            "São Paulo",
            "SP"
        ));

        List<AddressResponseDTO> result = addressService.getAddresses(0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verify addresses in database
        Address savedFirstAddress = addressRepository.findById(firstAddress.id()).orElse(null);
        Address savedSecondAddress = addressRepository.findById(secondAddress.id()).orElse(null);
        
        assertNotNull(savedFirstAddress);
        assertNotNull(savedSecondAddress);
        assertEquals(validAddressDTO.name(), savedFirstAddress.getName());
        assertEquals("Work", savedSecondAddress.getName());
    }

    @Test
    void getAddressesByCustomer_ShouldReturnCustomerAddresses() {
        // Create addresses for the customer
        AddressResponseDTO firstAddress = addressService.store(customer.getId(), validAddressDTO);
        AddressResponseDTO secondAddress = addressService.store(customer.getId(), new AddressRequestDTO(
            "Work",
            "Avenida Paulista",
            "1000",
            "01310-100",
            "São Paulo",
            "SP"
        ));

        // Create another customer with an address
        Customer anotherCustomer = new Customer();
        anotherCustomer.setName("Jane Doe");
        anotherCustomer.setEmail("jane@example.com");
        anotherCustomer.setCpf("98765432109");
        anotherCustomer = customerRepository.save(anotherCustomer);
        addressService.store(anotherCustomer.getId(), new AddressRequestDTO(
            "Home",
            "Rua Augusta",
            "500",
            "01304-000",
            "São Paulo",
            "SP"
        ));

        List<AddressResponseDTO> result = addressService.getAddressesByCustomer(customer.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        result.forEach(address -> assertEquals(customer.getId(), address.customerId()));
        
        // Verify addresses in database
        Address savedFirstAddress = addressRepository.findById(firstAddress.id()).orElse(null);
        Address savedSecondAddress = addressRepository.findById(secondAddress.id()).orElse(null);
        
        assertNotNull(savedFirstAddress);
        assertNotNull(savedSecondAddress);
        assertEquals(customer.getId(), savedFirstAddress.getCustomer().getId());
        assertEquals(customer.getId(), savedSecondAddress.getCustomer().getId());
    }
} 