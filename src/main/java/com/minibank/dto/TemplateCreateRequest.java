package com.minibank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TemplateCreateRequest(
        @NotNull Long userId,
        @NotBlank String name,
        @NotBlank String toAccount,
        @NotNull @Positive BigDecimal amount
) {}