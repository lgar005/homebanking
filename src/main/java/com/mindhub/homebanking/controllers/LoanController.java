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
import org.springframework.web.bind.annotation.*;

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
        Account account= accountService.findByNumber(loanApplicationDTO.getDestinationAccountNumber());
        Loan loan=loanService.findById(loanApplicationDTO.getLoanId());
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
       if(client.getClientLoans().stream().filter(clientLoan -> clientLoan.getLoan().getName()==loan.getName() && clientLoan.getAmountF()>0).collect(Collectors.toList()).size()==1){
           return new ResponseEntity<>("You cannot apply for the loan because you have already been approved for a similar loan.", HttpStatus.FORBIDDEN);
        }
        else{
            ClientLoan clientLoan= new ClientLoan(loanApplicationDTO.getAmount(), loanApplicationDTO.getPayments(), loanApplicationDTO.getAmount()*loan.getInterest());
           clientLoanService.saveClientLoan(clientLoan);
           client.addClientLoan(clientLoan);
            loan.addClientLoan(clientLoan);
           LocalDateTime dateCreated=LocalDateTime.now();
           DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
           String date1= fmt.format(dateCreated);
           LocalDateTime date1f=LocalDateTime.parse(date1,fmt);
           account.setBalance(account.getBalance()+loanApplicationDTO.getAmount());
            Transaction transaction= new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(),loan.getName()+" loan approved", date1f, account.getBalance());
            account.addTransaction(transaction);
            transactionService.saveTransaction(transaction);
            accountService.saveAccount(account);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @GetMapping("/api/loans")
    public List<LoanDTO> getAllLoans(){
        return loanService.getAllLoansDTO();
    }

    @PostMapping(path = "/api/loans/admin")
    public ResponseEntity<Object> createLoanType( @RequestBody Loan loan){
        if(loan.getName().isBlank()){
            return new ResponseEntity<>("Please enter the name of the loan to create", HttpStatus.FORBIDDEN);
        }
        if(loan.getMaxAmount()<=0){
            return new ResponseEntity<>("The loan amount cannot be zero", HttpStatus.FORBIDDEN);
        }
        if(loan.getPayments()==null){
            return new ResponseEntity<>("You have not set the quotas", HttpStatus.FORBIDDEN);
        }
        if(loan.getPayments().stream().map(payment-> payment>120 && payment<=0).collect(Collectors.toList()).size()!=loan.getPayments().size()){
            return new ResponseEntity<>("Some of the fees are greater than 120 or less than or equal to zero. Or it is not a numemo", HttpStatus.FORBIDDEN);
        }
        if(loanService.getAllLoansDTO().stream().filter(loanDTO -> loanDTO.getName()==loan.getName()).collect(Collectors.toList()).size()>0 ){
            return new ResponseEntity<>("Such a loan already exists", HttpStatus.FORBIDDEN);
        }
        Loan newLoan= new Loan(loan.getName(), loan.getMaxAmount(), loan.getPayments(), loan.getInterest());
        loanService.saveLoan(newLoan);
        return  new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @PostMapping(path = "/api/clients/current")
    public  ResponseEntity<Object> paymentOfLoans(Authentication authentication, @RequestParam  Long idclientLoan,@RequestParam String numberAccount, @RequestParam Double amount){
        Client client= clientService.getAuthenticatedClient(authentication);
        Account account= accountService.findByNumber(numberAccount);
        ClientLoan clientLoan= clientLoanService.getClientLoan(idclientLoan);
        if(account==null){
            return new ResponseEntity<>("The account was not found, please verify the information.", HttpStatus.FORBIDDEN);
        }if(clientLoan==null){
            return new ResponseEntity<>("The loan was not found.", HttpStatus.FORBIDDEN);
        }
        if(account.getBalance()<amount){
            return new ResponseEntity<>("The account balance must be greater than or equal to the amount of the installment.", HttpStatus.FORBIDDEN);
        }if(clientLoan.getPayments()==0){
            return new ResponseEntity<>("The loan has already been paid", HttpStatus.FORBIDDEN);
        }
        else{

            account.setBalance(account.getBalance()-amount);
            clientLoan.setAmountF(clientLoan.getAmountF()-amount);
            Transaction transaction= new Transaction(TransactionType.CREDIT, amount,"payment of the" +clientLoan.getLoan().getName(), LocalDateTime.now(),account.getBalance() );
            account.addTransaction(transaction);
            clientLoan.setPayments(clientLoan.getPayments()-1);
            transactionService.saveTransaction(transaction);
            return   new ResponseEntity<>("One installment of the loan has been paid", HttpStatus.OK);
        }
    }
}
