package com.minibank.controller;

import com.minibank.dto.TransferInitRequest;
import com.minibank.dto.TransactionRequest;
import com.minibank.dto.TransferResult;
import com.minibank.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/init")
    public TransferResult init(@Valid @RequestBody TransferInitRequest req) {
        return transferService.init(req);
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