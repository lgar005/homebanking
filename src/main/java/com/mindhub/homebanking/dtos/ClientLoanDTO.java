package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;

public class ClientLoanDTO {

    private  Long id;

    private Long loanId;

    private String nameLoan;
    private double amount;
    private Integer payments;

    public ClientLoanDTO(ClientLoan clientLoan){
        this.id=clientLoan.getId();
        this.loanId=clientLoan.getLoan().getId();
        this.nameLoan=clientLoan.getLoan().getName();
        this.amount=clientLoan.getAmount();
        this.payments=clientLoan.getPayments();
    }

    public Long getId() {
        return id;
    }

    public Long getLoanId() {
        return loanId;
    }

    public String getNameLoan() {
        return nameLoan;
    }

    public double getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }
}
