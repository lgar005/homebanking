package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/api/accounts")
    public List<AccountDTO> getAccounts(){
       return accountService.getAccountsDTO();
    }

    @GetMapping("/api/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        //accountRepository.findById(id).map(account -> new AccountDTO(account)).orElse(null);
        //new AccountDTO(accountRepository.findById(id).orElse(null))
        return accountService.getAccountDTO(id);
    }

    @GetMapping("/api/clients/current/accounts")
    public List<AccountDTO> getAccountsClient(Authentication authentication){
         Client client=clientService.getAuthenticatedClient(authentication);
         return client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());
    }

    @PostMapping(path ="/api/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication, AccountType accountType){
        Client client=clientService.getAuthenticatedClient(authentication);
        if(AccountType.CURRENT!=accountType &&	AccountType.SAVINGS!=accountType){
            return new ResponseEntity<>("Please select the type of account you want to create", HttpStatus.FORBIDDEN);
        }
        if( client.getAccounts().stream().filter(account -> account.getActive()).collect(Collectors.toList()).size()<=2){
            String rNumber=null;
            /*do{
                int randomNumber =  (int) (Math.random() * 10000000) + 1;
                rNumber="VIN-"+String.valueOf(randomNumber);
            }while(accountService.findByNumber(rNumber)!=null);*/
            Account account=new Account(accountService.numberAccountNoRepeat(), LocalDateTime.now() ,0.0,accountType);
            client.addAccount(account);
            accountService.saveAccount(account);
            return new ResponseEntity<>(HttpStatus.CREATED);

        }else{
            return new ResponseEntity<>("Maximum number of accounts allowed (3)", HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping(path = "/api/clients/current/accounts")
    public ResponseEntity<Object> deleteAccount(@RequestParam String accountNumber,Authentication authentication){
        Client client=clientService.getAuthenticatedClient(authentication);
        Account account= accountService.findByNumber(accountNumber);
        if(account==null){
            return  new ResponseEntity<>("The account does not exist ", HttpStatus.FORBIDDEN);
        } if(account.getClient()!=client){
            return  new ResponseEntity<>("The account you wish to delete does not belong to you", HttpStatus.FORBIDDEN);
        }if(account.getBalance()>0){
            return  new ResponseEntity<>("The account must have a zero balance, please send your money to another account.", HttpStatus.FORBIDDEN);
        }else{
            account.setActive(false);
            account.getTransactions().stream().forEach(transaction -> transaction.setActive(false));
            accountService.saveAccount(account);
            account.getTransactions().stream().forEach(transaction -> transactionService.saveTransaction(transaction));
            return new ResponseEntity<>("se elimino la cuenta",HttpStatus.OK);
        }
    }
}
