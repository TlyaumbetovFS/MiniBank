package com.minibank.repository;

import com.minibank.entity.OperationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OperationSessionRepository extends JpaRepository<OperationSession, UUID> {
}