package com.devJava.client_app.service;

import org.springframework.stereotype.Service;

@Service
public class CpfValidatorService {
    
    public boolean isValid(String cpf) {
        // Remove non-numeric characters
        cpf = cpf.replaceAll("[^0-9]", "");
        
        // Check if CPF has 11 digits
        if (cpf.length() != 11) {
            return false;
        }
        
        // Check if all digits are the same
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Validate first digit
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit > 9) {
            firstDigit = 0;
        }
        if (firstDigit != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }
        
        // Validate second digit
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit > 9) {
            secondDigit = 0;
        }
        return secondDigit == Character.getNumericValue(cpf.charAt(10));
    }
} 