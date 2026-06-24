package com.minibank.service;

import com.minibank.dto.TransferInitRequest;
import com.minibank.dto.TransactionRequest;
import com.minibank.dto.TransferResult;
import com.minibank.entity.Transaction;
import com.minibank.entity.TransactionStatus;
import com.minibank.entity.TransactionType;
import com.minibank.repository.AccountRepository;
import com.minibank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransferResult init(TransferInitRequest req) {
        var from = accountRepository.findByAccountNumber(req.fromAccount())
                .orElseThrow(() -> new IllegalStateException("Sender account not found"));

        if (req.type() == TransactionType.INTERNAL) {
            accountRepository.findByAccountNumber(req.toAccount())
                    .orElseThrow(() -> new IllegalStateException("Recipient account not found"));
        }

        if (from.getBalance().compareTo(req.amount()) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        var tx = new Transaction();
        tx.setFromAccount(req.fromAccount());
        tx.setToAccount(req.toAccount());
        tx.setAmount(req.amount());
        tx.setType(req.type());
        tx.setStatus(TransactionStatus.CREATED);
        transactionRepository.save(tx);

        return new TransferResult(tx.getId(), tx.getStatus(), tx.getAmount());
    }

    @Transactional
    public TransferResult confirm(TransactionRequest req) {
        var tx = transactionRepository.findById(req.transactionId())
                .orElseThrow(() -> new IllegalStateException("Transaction not found"));
        if (tx.getStatus() != TransactionStatus.CREATED) {
            throw new IllegalStateException("Transaction not in CREATED state");
        }
        tx.setStatus(TransactionStatus.CONFIRMED);
        transactionRepository.save(tx);
        return new TransferResult(tx.getId(), tx.getStatus(), tx.getAmount());
    }

    @Transactional
    public TransferResult execute(TransactionRequest req) {
        var tx = transactionRepository.findById(req.transactionId())
                .orElseThrow(() -> new IllegalStateException("Transaction not found"));
        if (tx.getStatus() != TransactionStatus.CONFIRMED) {
            throw new IllegalStateException("Transaction not in CONFIRMED state");
        }

        var from = accountRepository.findByAccountNumber(tx.getFromAccount())
                .orElseThrow(() -> new IllegalStateException("Sender account not found"));

        // повторно проверяем баланс - между confirm и execute он мог измениться
        if (from.getBalance().compareTo(tx.getAmount()) < 0) {
            tx.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(tx);
            throw new IllegalStateException("Insufficient funds");
        }

        // списываем у отправителя
        from.setBalance(from.getBalance().subtract(tx.getAmount()));
        accountRepository.save(from);

        if (tx.getType() == TransactionType.INTERNAL) {
            var to = accountRepository.findByAccountNumber(tx.getToAccount())
                    .orElseThrow(() -> new IllegalStateException("Recipient account not found"));
            to.setBalance(to.getBalance().add(tx.getAmount()));
            accountRepository.save(to);
        }

        tx.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(tx);
        return new TransferResult(tx.getId(), tx.getStatus(), tx.getAmount());
    }
}