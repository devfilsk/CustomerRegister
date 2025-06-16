package com.devJava.client_app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CpfValidatorServiceTest {

    private final CpfValidatorService cpfValidatorService = new CpfValidatorService();

    @Test
    void isValid_WithValidCPF_ShouldReturnTrue() {
        assertTrue(cpfValidatorService.isValid("52998224725"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "52998224725", // Valid CPF
        "529.982.247-25", // Valid CPF with formatting
        "52998224725" // Valid CPF without formatting
    })
    void isValid_WithValidFormattedCPF_ShouldReturnTrue(String cpf) {
        assertTrue(cpfValidatorService.isValid(cpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "12345678901", // Invalid CPF
        "11111111111", // All same digits
        "123", // Too short
        "123456789012", // Too long
        "abc.def.ghi-jk", // Non-numeric characters
        "" // Empty string
    })
    void isValid_WithInvalidCPF_ShouldReturnFalse(String cpf) {
        assertFalse(cpfValidatorService.isValid(cpf));
    }

    @Test
    void isValid_WithNullCPF_ShouldReturnFalse() {
        assertFalse(cpfValidatorService.isValid(null));
    }
} 