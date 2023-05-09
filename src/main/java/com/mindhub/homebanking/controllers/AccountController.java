package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
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
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @RequestMapping("/api/accounts")
    public List<AccountDTO> getAccounts(){
       return accountService.getAccountsDTO();
    }

    @RequestMapping("/api/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        //accountRepository.findById(id).map(account -> new AccountDTO(account)).orElse(null);
        //new AccountDTO(accountRepository.findById(id).orElse(null))
        return accountService.getAccountDTO(id);
    }

   @RequestMapping("/api/clients/current/accounts")
    public List<AccountDTO> getAccountsClient(Authentication authentication){
         Client client=clientService.getAuthenticatedClient(authentication);
         return client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());
    }

    /*****/
    @PostMapping(path ="/api/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication){
        Client client=clientService.getAuthenticatedClient(authentication);
        if(client.getAccounts().size()<=2 ){
            String rNumber=null;
            do{
                int randomNumber =  (int) (Math.random() * 10000000) + 1;
                rNumber="VIN-"+String.valueOf(randomNumber);
            }while(accountService.findByNumber(rNumber)!=null);
            Account account=new Account(rNumber, LocalDateTime.now() ,0.0);
            client.addAccount(account);
            accountService.saveAccount(account);
            return new ResponseEntity<>(HttpStatus.CREATED);

        }else{
            return new ResponseEntity<>("Maximum number of accounts allowed (3)", HttpStatus.FORBIDDEN);
        }
    }






}
