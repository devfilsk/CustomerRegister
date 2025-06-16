package com.devJava.client_app.service;

import com.devJava.client_app.domain.address.AddressResponseDTO;
import com.devJava.client_app.domain.customer.Customer;
import com.devJava.client_app.domain.customer.CustomerRequestDTO;
import com.devJava.client_app.domain.customer.CustomerResponseDTO;
import com.devJava.client_app.domain.phone.Phone;
import com.devJava.client_app.domain.phone.PhoneRequestDTO;
import com.devJava.client_app.domain.phone.PhoneResponseDTO;
import com.devJava.client_app.repository.CustomerRepository;
import com.devJava.client_app.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PhoneService {
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private CustomerRepository customerRepository;

    public PhoneResponseDTO store(UUID customerId, PhoneRequestDTO data) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Phone phone = new Phone();
        phone.setNumber(data.number());
        phone.setCustomer(customer);
        
        Phone savedPhone = phoneRepository.save(phone);
        
        return new PhoneResponseDTO(
            savedPhone.getId(),
            savedPhone.getNumber(),
            savedPhone.getCustomer().getId(),
            savedPhone.getCreatedAt(),
            savedPhone.getUpdatedAt()
        );
    }

    public PhoneResponseDTO update(UUID id, PhoneRequestDTO data) {
        Phone phone = phoneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phone not found"));

        // Validate name length
        if (data.number().length() != 10 && data.number().length() != 11) {
            throw new IllegalArgumentException("Phone number invalid");
        }

        phone.setNumber(data.number());

        Phone updatedPhone = phoneRepository.save(phone);
        return this.convertToDTO(updatedPhone);
    }

    public void delete(UUID id) {
        if (!phoneRepository.existsById(id)) {
            throw new IllegalArgumentException("Phone not found");
        }
        phoneRepository.deleteById(id);
    }

    private PhoneResponseDTO convertToDTO(Phone phone) {
        return new PhoneResponseDTO(
                phone.getId(),
                phone.getNumber(),
                phone.getCustomer().getId(),
                phone.getCreatedAt(),
                phone.getUpdatedAt()
        );
    }
}
