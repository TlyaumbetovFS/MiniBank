package com.minibank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentInitRequest(
        @NotNull Long templateId,
        @NotBlank String fromAccount
) {}