package com.minibank.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TransactionRequest(
        @NotNull UUID transactionId
) {}