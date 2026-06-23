package com.minibank.entity;

public enum TransactionStatus {
    CREATED,    // init
    CONFIRMED,  // confirm
    COMPLETED,  // execute — деньги переведены
    FAILED
}