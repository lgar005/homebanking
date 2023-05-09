package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Loan;

import javax.persistence.ElementCollection;
import java.util.List;

public class LoanDTO {

    private Long id;
    private String name;
    private double  maxAmount;

    private double interest;
    private List<Integer> payments;
    public LoanDTO(Loan loan){
        this.id=loan.getId();
        this.name=loan.getName();
        this.maxAmount=loan.getMaxAmount();
        this.payments=loan.getPayments();
        this.payments=loan.getPayments();
        this.interest=loan.getInterest();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public double getInterest() {
        return interest;
    }
}
