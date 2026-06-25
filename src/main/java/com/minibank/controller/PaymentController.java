package com.minibank.controller;

import com.minibank.dto.PaymentInitRequest;
import com.minibank.dto.TemplateCreateRequest;
import com.minibank.dto.TransactionRequest;
import com.minibank.dto.TransferResult;
import com.minibank.service.PaymentService;
import com.minibank.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final TransferService transferService;

    @PostMapping("/template")
    public Map<String, Long> createTemplate(@Valid @RequestBody TemplateCreateRequest req) {
        return Map.of("templateId", paymentService.createTemplate(req));
    }

    @PostMapping("/init")
    public TransferResult init(@Valid @RequestBody PaymentInitRequest req) {
        return paymentService.init(req);
    }

    @PostMapping("/confirm")
    public TransferResult confirm(@Valid @RequestBody TransactionRequest req) {
        return transferService.confirm(req);
    }

    @PostMapping("/execute")
    public TransferResult execute(@Valid @RequestBody TransactionRequest req) {
        return transferService.execute(req);
    }
}