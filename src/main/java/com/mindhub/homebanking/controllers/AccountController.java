package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/api/accounts")
    public List<AccountDTO> getAccounts(){
       return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());
    }

    @RequestMapping("/api/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        return accountRepository.findById(id).map(account -> new AccountDTO(account)).orElse(null);
    }

   @RequestMapping("/api/clients/current/accounts")
    public List<AccountDTO> getAccounts(Authentication authentication){
         Client client=clientRepository.findByEmail(authentication.getName());
         return client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());
    }

    /*****/
    @PostMapping(path ="/api/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication){
        Client client=clientRepository.findByEmail(authentication.getName());
        if(client.getAccounts().size()<=2 ){
            String rNumber=null;
            do{
                int randomNumber =  (int) (Math.random() * 10000000) + 1;
                rNumber="VIN-"+String.valueOf(randomNumber);
            }while(accountRepository.findByNumber(rNumber)!=null);
            Account account=new Account(rNumber, LocalDateTime.now() ,0.0);
            client.addAccount(account);
            accountRepository.save(account);
            return new ResponseEntity<>(HttpStatus.CREATED);

        }else{
            return new ResponseEntity<>("Maximum number of accounts allowed (3)", HttpStatus.FORBIDDEN);
        }
    }






}
