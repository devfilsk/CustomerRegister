package com.devJava.client_app.service;

import com.devJava.client_app.domain.address.AddressResponseDTO;
import com.devJava.client_app.domain.customer.Customer;
import com.devJava.client_app.domain.customer.CustomerRequestDTO;
import com.devJava.client_app.domain.customer.CustomerResponseDTO;
import com.devJava.client_app.domain.phone.PhoneResponseDTO;
import com.devJava.client_app.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CpfValidatorService cpfValidatorService;

    public CustomerResponseDTO store(CustomerRequestDTO data) {
        // Validate CPF
        if (!cpfValidatorService.isValid(data.cpf())) {
            throw new IllegalArgumentException("Invalid CPF");
        }

        // Check if CPF already exists
        if (customerRepository.existsByCpf(data.cpf())) {
            throw new IllegalArgumentException("CPF already registered");
        }

        // Check if email already exists
        if (customerRepository.existsByEmail(data.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Customer customer = new Customer();
        customer.setName(data.name());
        customer.setEmail(data.email());
        customer.setCpf(data.cpf());

        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }

    public CustomerResponseDTO update(UUID id, CustomerRequestDTO data) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Validate name length
        if (data.name().length() < 10) {
            throw new IllegalArgumentException("Customer name must have at least 10 characters");
        }

        // Validate CPF
        if (!cpfValidatorService.isValid(data.cpf())) {
            throw new IllegalArgumentException("Invalid CPF");
        }

        // Check if CPF already exists for another customer
        if (!data.cpf().equals(customer.getCpf()) && customerRepository.existsByCpf(data.cpf())) {
            throw new IllegalArgumentException("CPF already registered");
        }

        customer.setName(data.name());
        customer.setEmail(data.email());
        customer.setCpf(data.cpf());

        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDTO(updatedCustomer);
    }

    public Page<CustomerResponseDTO> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable).map(this::convertToDTO);
    }

    public CustomerResponseDTO findById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return convertToDTO(customer);
    }

    public void delete(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found");
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponseDTO convertToDTO(Customer customer) {
        return new CustomerResponseDTO(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getCpf(),
            customer.getPhones().stream()
                .map(phone -> new PhoneResponseDTO(
                    phone.getId(),
                    phone.getNumber(),
                    customer.getId(),
                    phone.getCreatedAt(),
                    phone.getUpdatedAt()
                ))
                .collect(Collectors.toList()),
            customer.getAddresses().stream()
                .map(address -> new AddressResponseDTO(
                    address.getId(),
                    address.getName(),
                    address.getStreet(),
                    address.getNumber(),
                    address.getPostcode(),
                    address.getCity(),
                    address.getUf(),
                    address.getCustomer().getId(),
                    address.getCreatedAt(),
                    address.getUpdatedAt()
                ))
                .collect(Collectors.toList()),
            customer.getCreatedAt(),
            customer.getUpdatedAt()
        );
    }
}
