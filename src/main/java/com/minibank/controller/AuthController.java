package com.minibank.controller;

import com.minibank.dto.LoginInitRequest;
import com.minibank.dto.LoginResult;
import com.minibank.dto.VerifyRequest;
import com.minibank.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/init")
    public Map<String, UUID> loginInit(@Valid @RequestBody LoginInitRequest req) {
        return Map.of("sessionId", authService.init(req));
    }

    @PostMapping("/login/confirm")
    public LoginResult loginConfirm(@Valid @RequestBody VerifyRequest req) {
        return authService.confirm(req);
    }
}