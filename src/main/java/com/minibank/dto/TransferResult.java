package com.minibank.dto;

import com.minibank.entity.TransactionStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferResult(
        UUID transactionId,
        TransactionStatus status,
        BigDecimal amount
) {}