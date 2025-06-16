package com.devJava.client_app.service;

import com.devJava.client_app.domain.address.Address;
import com.devJava.client_app.domain.address.AddressRequestDTO;
import com.devJava.client_app.domain.address.AddressResponseDTO;
import com.devJava.client_app.domain.customer.Customer;
import com.devJava.client_app.domain.customer.CustomerRequestDTO;
import com.devJava.client_app.domain.customer.CustomerResponseDTO;
import com.devJava.client_app.repository.AddressRepository;
import com.devJava.client_app.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CustomerRepository customerRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public AddressResponseDTO store(UUID customerId, AddressRequestDTO data) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Address newAddress = new Address();
        newAddress.setName(data.name());
        newAddress.setCity(data.city());
        newAddress.setStreet(data.street());
        newAddress.setNumber(data.number());
        newAddress.setPostcode(data.postcode());
        newAddress.setUf(data.uf());
        newAddress.setCustomer(customer);

        Address savedAddress = addressRepository.save(newAddress);
        return convertToDTO(savedAddress);
    }

    public List<AddressResponseDTO> getAddresses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Address> addressPage = addressRepository.findAll(pageable);
        return addressPage.map(this::convertToDTO).stream().toList();
    }

    public List<AddressResponseDTO> getAddressesByCustomer(UUID customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Address> addressPage = addressRepository.addressesByCustomer(customerId, pageable);
        return addressPage.map(this::convertToDTO).stream().toList();
    }

    public AddressResponseDTO update(UUID id, AddressRequestDTO data) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Validate name length
        if (data.name().length() < 10) {
            throw new IllegalArgumentException("Customer name must have at least 10 characters");
        }

        Address newAddress = new Address();
        newAddress.setName(data.name());
        newAddress.setCity(data.city());
        newAddress.setStreet(data.street());
        newAddress.setNumber(data.number());
        newAddress.setPostcode(data.postcode());
        newAddress.setUf(data.uf());
        newAddress.setCustomer(address.getCustomer());

        Address updatedCustomer = addressRepository.save(newAddress);
        return convertToDTO(updatedCustomer);
    }

    public void delete(UUID id) {
        if (!addressRepository.existsById(id)) {
            throw new IllegalArgumentException("Address not found");
        }
        addressRepository.deleteById(id);
    }

    private AddressResponseDTO convertToDTO(Address address) {
        return new AddressResponseDTO(
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
        );
    }
}
