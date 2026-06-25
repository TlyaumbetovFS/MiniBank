package com.minibank.service;

import com.minibank.dto.PaymentInitRequest;
import com.minibank.dto.TemplateCreateRequest;
import com.minibank.dto.TransferResult;
import com.minibank.entity.Template;
import com.minibank.entity.Transaction;
import com.minibank.entity.TransactionStatus;
import com.minibank.entity.TransactionType;
import com.minibank.repository.AccountRepository;
import com.minibank.repository.TemplateRepository;
import com.minibank.repository.TransactionRepository;
import com.minibank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TemplateRepository templateRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createTemplate(TemplateCreateRequest req) {
        var user = userRepository.findById(req.userId())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        var template = new Template();
        template.setName(req.name());
        template.setToAccount(req.toAccount());
        template.setAmount(req.amount());
        template.setUser(user);
        templateRepository.save(template);
        return template.getId();
    }

    @Transactional
    public TransferResult init(PaymentInitRequest req) {
        var template = templateRepository.findById(req.templateId())
                .orElseThrow(() -> new IllegalStateException("Template not found"));
        var from = accountRepository.findByAccountNumber(req.fromAccount())
                .orElseThrow(() -> new IllegalStateException("Sender account not found"));

        if (from.getBalance().compareTo(template.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        var tx = new Transaction();
        tx.setFromAccount(req.fromAccount());
        tx.setToAccount(template.getToAccount());   // из шаблона
        tx.setAmount(template.getAmount());         // из шаблона
        tx.setType(TransactionType.PAYMENT);
        tx.setStatus(TransactionStatus.CREATED);
        transactionRepository.save(tx);

        return new TransferResult(tx.getId(), tx.getStatus(), tx.getAmount());
    }
}