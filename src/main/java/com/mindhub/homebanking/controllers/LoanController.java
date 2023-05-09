package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LoanController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountService accountService;

   @Autowired
    private LoanService loanService;

    @Autowired
    private ClientLoanService clientLoanService;

    @Autowired
    private TransactionService transactionService;


    @Transactional
    @PostMapping(value="/api/loans")
    public ResponseEntity<String> createLoan(Authentication authentication, @RequestBody LoanApplicationDTO loanApplicationDTO) {
        Client client=clientService.getAuthenticatedClient(authentication);
        // accountRepository.findByNumber(loanApplicationDTO.getDestinationAccountNumber())
        Account account= accountService.findByNumber(loanApplicationDTO.getDestinationAccountNumber());
        Loan loan=loanService.findById(loanApplicationDTO.getLoanId());
                //loanRepository.findById(loanApplicationDTO.getLoanId()).orElse(null);
        if(loanApplicationDTO.getLoanId().equals(null)){
            return new ResponseEntity<>("you did not select a loan", HttpStatus.FORBIDDEN);
        }
        if(loanApplicationDTO.getPayments().equals(null)){
            return new ResponseEntity<>("you did not select a payment", HttpStatus.FORBIDDEN);
        }
       if(loanApplicationDTO.getDestinationAccountNumber().isBlank()){
           return new ResponseEntity<>("You have not selected an account", HttpStatus.FORBIDDEN);
       }
        if(loan==null){
            return new ResponseEntity<>("Loan not found", HttpStatus.FORBIDDEN);
        }
        if(loanApplicationDTO.getAmount()<=0){
            return new ResponseEntity<>("The amount requested must be greater than zero", HttpStatus.FORBIDDEN);
        }
        if(account==null){
            return new ResponseEntity<>("Account not found", HttpStatus.FORBIDDEN);
        }
        if(account.getClient()!=client){
            return new ResponseEntity<>("The account does not belong to you", HttpStatus.FORBIDDEN);
        }
        if(loan.getMaxAmount()<=loanApplicationDTO.getAmount()){
            return new ResponseEntity<>("The requested amount exceeds the maximum amount", HttpStatus.FORBIDDEN);
        }if(!loan.getPayments().contains(loanApplicationDTO.getPayments())){
            return new ResponseEntity<>("Number of quotas does not match any available", HttpStatus.FORBIDDEN);
        }
       if(client.getClientLoans().stream().filter(clientLoan -> clientLoan.getLoan().getName()==loan.getName()).collect(Collectors.toList()).size()==1){
           return new ResponseEntity<>("You cannot apply for the loan because you have already been approved for a similar loan.", HttpStatus.FORBIDDEN);
        }
        else{
            ClientLoan clientLoan= new ClientLoan(loanApplicationDTO.getAmount()+(loanApplicationDTO.getAmount()*0.20), loanApplicationDTO.getPayments());
            //clientLoanRepository.save(clientLoan);
           clientLoanService.saveClientLoan(clientLoan);
           client.addClientLoan(clientLoan);
            loan.addClientLoan(clientLoan);
           LocalDateTime dateCreated=LocalDateTime.now();
           DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
           String date1= fmt.format(dateCreated);
           LocalDateTime date1f=LocalDateTime.parse(date1,fmt);
            Transaction transaction= new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(),loan.getName()+" loan approved", date1f);
            account.addTransaction(transaction);
            //transactionRepository.save(transaction);
            transactionService.saveTransaction(transaction);
            account.setBalance(account.getBalance()+loanApplicationDTO.getAmount());
            //accountRepository.save(account);
            accountService.saveAccount(account);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @RequestMapping("/api/loans")
    public List<LoanDTO> getAllLoans(){
        return loanService.getAllLoansDTO();
    }
}
