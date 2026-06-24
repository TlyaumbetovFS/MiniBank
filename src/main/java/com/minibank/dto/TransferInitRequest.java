package com.minibank.dto;

import com.minibank.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TransferInitRequest(
        @NotBlank String fromAccount,
        @NotBlank String toAccount,
        @NotNull @Positive BigDecimal amount,
        @NotNull TransactionType type
) {}