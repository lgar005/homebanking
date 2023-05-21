package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;

import java.time.LocalDateTime;

public class TransactionDTO {
    private Long id;
    private TransactionType type;
    private double amount;
    private String description;
    private LocalDateTime data;

    private double balance;

    private boolean active;

    public TransactionDTO(Transaction transaction) {
        this.id=transaction.getId();
        this.type=transaction.getType();
        this.amount=transaction.getAmount();
        this.description=transaction.getDescription();
        this.data=transaction.getData();
        this.balance=transaction.getBalance();
        this.active=transaction.getActive();

    }

    public Long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }


    public LocalDateTime getData() {
        return data;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }
}
