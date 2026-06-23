package com.minibank.entity;

public enum SessionStatus {
    PENDING,    // создана, код не подтверждён
    CONFIRMED   // код подтверждён, ждём финальный шаг
}