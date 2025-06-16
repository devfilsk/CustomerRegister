package com.devJava.client_app.controller;

import com.devJava.client_app.domain.customer.CustomerRequestDTO;
import com.devJava.client_app.domain.customer.CustomerResponseDTO;
import com.devJava.client_app.domain.phone.Phone;
import com.devJava.client_app.domain.phone.PhoneRequestDTO;
import com.devJava.client_app.domain.phone.PhoneResponseDTO;
import com.devJava.client_app.service.PhoneService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/phones")
@Tag(name = "Phone", description = "Phone management APIs")
public class PhoneController {

    @Autowired
    private PhoneService phoneService;

    @Operation(
        summary = "Add a new phone to a customer",
        description = "Adds a new phone number to an existing customer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Phone added successfully",
            content = @Content(schema = @Schema(implementation = PhoneResponseDTO.class))
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
    public ResponseEntity<PhoneResponseDTO> create(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable UUID customerId,
            @Parameter(description = "Phone data", required = true)
            @Valid @RequestBody PhoneRequestDTO body) {
        PhoneResponseDTO phone = this.phoneService.store(customerId, body);
        return ResponseEntity.ok(phone);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhoneResponseDTO> update(@PathVariable UUID id, @RequestBody PhoneRequestDTO body) {
        PhoneResponseDTO updatedPhone = this.phoneService.update(id, body);
        return ResponseEntity.ok().body(updatedPhone);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        this.phoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
