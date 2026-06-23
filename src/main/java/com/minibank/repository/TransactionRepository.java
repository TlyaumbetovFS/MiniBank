package com.minibank.repository;

import com.minibank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}