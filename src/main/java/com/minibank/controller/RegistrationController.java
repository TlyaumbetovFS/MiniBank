package com.minibank.controller;

import com.minibank.dto.RegistrationInitRequest;
import com.minibank.dto.RegistrationResult;
import com.minibank.dto.SessionRequest;
import com.minibank.dto.VerifyRequest;
import com.minibank.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/init")
    public Map<String, UUID> init(@Valid @RequestBody RegistrationInitRequest req) {
        return Map.of("sessionId", registrationService.init(req));
    }

    @PostMapping("/verify")
    public Map<String, String> verify(@Valid @RequestBody VerifyRequest req) {
        registrationService.verify(req);
        return Map.of("status", "confirmed");
    }

    @PostMapping("/complete")
    public RegistrationResult complete(@Valid @RequestBody SessionRequest req) {
        return registrationService.complete(req);
    }
}