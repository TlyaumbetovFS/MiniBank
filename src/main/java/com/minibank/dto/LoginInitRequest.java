package com.minibank.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginInitRequest(
        @NotBlank String phone
) {}