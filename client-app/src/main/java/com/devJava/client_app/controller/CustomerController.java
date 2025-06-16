package com.devJava.client_app.controller;

import com.devJava.client_app.domain.customer.CustomerRequestDTO;
import com.devJava.client_app.domain.customer.CustomerResponseDTO;
import com.devJava.client_app.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer", description = "Customer management APIs")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Operation(
        summary = "Create a new customer",
        description = "Creates a new customer with the provided information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Customer created successfully",
            content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        )
    })
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(
            @Parameter(description = "Customer data", required = true)
            @Valid @RequestBody CustomerRequestDTO body) {
        CustomerResponseDTO customer = this.customerService.store(body);
        return ResponseEntity.ok(customer);
    }

    @Operation(
        summary = "Update an existing customer",
        description = "Updates an existing customer's information by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Customer updated successfully",
            content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))
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
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Updated customer data", required = true)
            @Valid @RequestBody CustomerRequestDTO body) {
        CustomerResponseDTO customer = this.customerService.update(id, body);
        return ResponseEntity.ok(customer);
    }

    @Operation(
        summary = "List all customers",
        description = "Retrieves a paginated list of all customers"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of customers retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping
    public ResponseEntity<Page<CustomerResponseDTO>> list(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Page<CustomerResponseDTO> customers = this.customerService.list(page, size);
        return ResponseEntity.ok(customers);
    }

    @Operation(
        summary = "Get customer by ID",
        description = "Retrieves a customer's information by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Customer found",
            content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Customer not found"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> findById(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable UUID id) {
        CustomerResponseDTO customer = this.customerService.findById(id);
        return ResponseEntity.ok(customer);
    }

    @Operation(
        summary = "Delete a customer",
        description = "Deletes a customer by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Customer deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Customer not found"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable UUID id) {
        this.customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
