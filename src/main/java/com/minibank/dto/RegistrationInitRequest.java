package com.minibank.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistrationInitRequest(
        @NotBlank String fullName,
        @NotBlank String passportData,
        @NotBlank String phone
) {}