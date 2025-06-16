package com.devJava.client_app.service;

import com.devJava.client_app.domain.customer.Customer;
import com.devJava.client_app.domain.phone.Phone;
import com.devJava.client_app.domain.phone.PhoneRequestDTO;
import com.devJava.client_app.domain.phone.PhoneResponseDTO;
import com.devJava.client_app.repository.CustomerRepository;
import com.devJava.client_app.repository.PhoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PhoneServiceTest {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private PhoneService phoneService;

    private PhoneRequestDTO validPhoneDTO;
    private Customer customer;

    @BeforeEach
    void setUp() {
        validPhoneDTO = new PhoneRequestDTO("62986447722");

        customer = new Customer();
        customer.setName("Filipe Maciel");
        customer.setEmail("filipe@example.com");
        customer.setCpf("12345678909");
        customer = customerRepository.save(customer);
    }

    @Test
    void store_WithValidData_ShouldCreatePhone() {
        PhoneResponseDTO result = phoneService.store(customer.getId(), validPhoneDTO);
        
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(validPhoneDTO.number(), result.number());
        assertEquals(customer.getId(), result.customerId());

        Phone savedPhone = phoneRepository.findById(result.id()).orElse(null);
        assertNotNull(savedPhone);
        assertEquals(result.id(), savedPhone.getId());
        assertEquals(validPhoneDTO.number(), savedPhone.getNumber());
        assertEquals(customer.getId(), savedPhone.getCustomer().getId());
    }

    @Test
    void store_WithNonExistentCustomer_ShouldThrowException() {
        UUID nonExistentCustomerId = UUID.randomUUID();
        assertThrows(RuntimeException.class, () -> 
            phoneService.store(nonExistentCustomerId, validPhoneDTO)
        );
    }

//    @Test
//    void store_WithDuplicatePhoneNumber_ShouldThrowException() {
//        // First phone
//        phoneService.store(customer.getId(), validPhoneDTO);
//
//        // Try to create second phone with same number
//        assertThrows(RuntimeException.class, () ->
//            phoneService.store(customer.getId(), validPhoneDTO)
//        );
//    }

    @Test
    void store_MultiplePhonesForSameCustomer_ShouldSucceed() {
        // First phone
        PhoneResponseDTO firstPhone = phoneService.store(customer.getId(), validPhoneDTO);

        // Second phone
        PhoneRequestDTO secondPhoneDTO = new PhoneRequestDTO("62986447723");
        PhoneResponseDTO secondPhone = phoneService.store(customer.getId(), secondPhoneDTO);

        assertNotNull(firstPhone);
        assertNotNull(secondPhone);
        assertNotEquals(firstPhone.id(), secondPhone.id());
        assertEquals(customer.getId(), firstPhone.customerId());
        assertEquals(customer.getId(), secondPhone.customerId());

        // Verify both phones are saved in database
        Phone savedFirstPhone = phoneRepository.findById(firstPhone.id()).orElse(null);
        Phone savedSecondPhone = phoneRepository.findById(secondPhone.id()).orElse(null);
        
        assertNotNull(savedFirstPhone);
        assertNotNull(savedSecondPhone);
        assertEquals(validPhoneDTO.number(), savedFirstPhone.getNumber());
        assertEquals(secondPhoneDTO.number(), savedSecondPhone.getNumber());
    }
}