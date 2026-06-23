package com.minibank.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SessionRequest(
        @NotNull UUID sessionId
) {}