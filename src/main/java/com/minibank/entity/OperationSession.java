package com.minibank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "operation_sessions")
@Getter
@Setter
public class OperationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;              // это и есть sessionId

    @Column(nullable = false)
    private String phone;         // к кому относится сессия

    @Column(nullable = false)
    private String otpCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionType type;     // REGISTRATION / LOGIN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status; // PENDING / CONFIRMED

    @Column
    private String payload;       // для регистрации: ФИО+паспорт во временном хранении

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}