package com.devJava.client_app.controller;

import com.devJava.client_app.domain.address.AddressRequestDTO;
import com.devJava.client_app.domain.address.AddressResponseDTO;
import com.devJava.client_app.domain.customer.CustomerRequestDTO;
import com.devJava.client_app.domain.customer.CustomerResponseDTO;
import com.devJava.client_app.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses")
@Tag(name = "Address", description = "Address management APIs")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Operation(
        summary = "Add a new address to a customer",
        description = "Adds a new address to an existing customer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Address added successfully",
            content = @Content(schema = @Schema(implementation = AddressResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Customer not found"
        )
    })
    @PostMapping("/{customerId}")
    public ResponseEntity<AddressResponseDTO> create(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable UUID customerId,
            @Parameter(description = "Address data", required = true)
            @Valid @RequestBody AddressRequestDTO body) {
        AddressResponseDTO address = this.addressService.store(customerId, body);
        return ResponseEntity.ok(address);
    }

    @Operation(
        summary = "List all addresses",
        description = "Retrieves a paginated list of all addresses"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of addresses retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> listAddresses(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        List<AddressResponseDTO> allAddresses = this.addressService.getAddresses(page, size);
        return ResponseEntity.ok(allAddresses);
    }

    @Operation(
        summary = "List customer addresses",
        description = "Retrieves a paginated list of addresses for a specific customer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of customer addresses retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Customer not found"
        )
    })
    @GetMapping("/{customerId}/all")
    public ResponseEntity<List<AddressResponseDTO>> customerAddresses(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable UUID customerId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        List<AddressResponseDTO> allAddresses = this.addressService.getAddressesByCustomer(customerId, page, size);
        return ResponseEntity.ok(allAddresses);
    }

    @Operation(
            summary = "Update an customer address",
            description = "Updates an customer address's information by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Address updated successfully",
                    content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> update(
            @Parameter(description = "Address ID", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Updated customer address", required = true)
            @Valid @RequestBody AddressRequestDTO body) {
        AddressResponseDTO customer = this.addressService.update(id, body);
        return ResponseEntity.ok(customer);
    }

    @Operation(
            summary = "Delete a customer address",
            description = "Deletes a customer address by their address ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Address deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Address ID", required = true)
            @PathVariable UUID id) {
        this.addressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
