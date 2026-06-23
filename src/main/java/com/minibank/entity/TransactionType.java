package com.minibank.entity;

public enum TransactionType {
    INTERNAL,   // получатель есть в БД
    EXTERNAL,   // получателя в БД нет (заглушка)
    PAYMENT     // платёж по шаблону
}