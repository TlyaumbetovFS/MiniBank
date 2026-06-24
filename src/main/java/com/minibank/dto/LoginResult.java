package com.minibank.dto;

import java.util.UUID;

public record LoginResult(UUID token, Long userId) {}