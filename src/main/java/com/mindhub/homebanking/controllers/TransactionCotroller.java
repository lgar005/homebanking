package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Controller
public class TransactionCotroller {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    @PostMapping(path = "/api/transactions")
    public ResponseEntity<Object> makeTransaction(Authentication authentication, @RequestParam Double amount, @RequestParam String description,@RequestParam String originAccountNumber, @RequestParam String destinationAccountNumber){
        Client client= clientRepository.findByEmail(authentication.getName());
        Account originAccount=accountRepository.findByNumber(originAccountNumber);
        Account destinationAccount=accountRepository.findByNumber(destinationAccountNumber);
        if(client==null){
            return  new ResponseEntity<>("The client was not found", HttpStatus.FORBIDDEN);
        }
        if(amount<=0){
            return  new ResponseEntity<>("Amount must have a value greater than zero", HttpStatus.FORBIDDEN);
        }
        if(amount.isNaN()){
            return  new ResponseEntity<>("Please enter a numerical value", HttpStatus.FORBIDDEN);
        }
        if(description.isBlank()){
            return  new ResponseEntity<>("Please fill in the description", HttpStatus.FORBIDDEN);
        }
        if(originAccountNumber.isBlank()){
            return  new ResponseEntity<>("Please fill in origin account", HttpStatus.FORBIDDEN);
        }
        if(destinationAccountNumber.isBlank()){
            return  new ResponseEntity<>("Please fill in the destination account", HttpStatus.FORBIDDEN);
        }
        if(originAccountNumber.equals(destinationAccountNumber)){
                return  new ResponseEntity<>("The origin and destination account numbers must be different.", HttpStatus.FORBIDDEN);
        }if(originAccount==null){
            return  new ResponseEntity<>("The source account does not exist", HttpStatus.FORBIDDEN);
        }if(originAccount.getClient()!=client){
            return  new ResponseEntity<>("The account does not belong to you", HttpStatus.FORBIDDEN);
        }if(destinationAccount==null){
            return  new ResponseEntity<>("The destination account does not exist", HttpStatus.FORBIDDEN);
        }if(originAccount.getBalance()<amount){
            return  new ResponseEntity<>("The amount to be transferred is greater than the available balance", HttpStatus.FORBIDDEN);
        }else{
            LocalDateTime dateCreated=LocalDateTime.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
            String date1= fmt.format(dateCreated);
            LocalDateTime date1f=LocalDateTime.parse(date1,fmt);
            Transaction debitTransaction= new Transaction(TransactionType.DEBIT, amount*-1, description + " "+ originAccountNumber, date1f);
            Transaction creditTransaction= new Transaction(TransactionType.CREDIT, amount, description+" "+ destinationAccountNumber, date1f);
            originAccount.addTransaction(debitTransaction);
            destinationAccount.addTransaction(creditTransaction);
            transactionRepository.save(debitTransaction);
            transactionRepository.save(creditTransaction);
            originAccount.setBalance(originAccount.getBalance()-amount);
            destinationAccount.setBalance(destinationAccount.getBalance()+amount);
            return new ResponseEntity<>("transaction ", HttpStatus.CREATED);
        }


    }
}
