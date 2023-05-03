package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.util.stream.Collectors.toList;

@RestController
public class ClientController {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/api/clients")
    public List<ClientDTO> getClients(){
        return clientRepository.findAll().stream().map(client -> new ClientDTO(client)).collect(toList());
    }
    @RequestMapping("/api/clients/{id}")
    public Optional<ClientDTO> getClient(@PathVariable Long id){
        return  clientRepository.findById(id).map(client -> new ClientDTO(client));
    }

    @RequestMapping("/api/clients/current")
    public ClientDTO getClient(Authentication authentication) {
       Client client=clientRepository.findByEmail(authentication.getName());
        ClientDTO clientDTO= new ClientDTO(client);
        return clientDTO;
    }
    @PostMapping(path = "api/clients")
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {
       if(firstName.isBlank()){
           return new ResponseEntity<>("The first name is required", HttpStatus.FORBIDDEN);
       }
        if(lastName.isBlank()){
            return new ResponseEntity<>("The last name is required", HttpStatus.FORBIDDEN);
        }
        if(email.isBlank()){
            return new ResponseEntity<>("The email is required", HttpStatus.FORBIDDEN);
        }
        if(password.isBlank()){
            return new ResponseEntity<>("The password is requirede", HttpStatus.FORBIDDEN);
        }
        if(!firstName.matches("^[a-zA-Z]*$") ){
            return new ResponseEntity<>("First name is not valid. The name can only contain letters", HttpStatus.FORBIDDEN);
        }
        if(!lastName.matches("^[a-zA-Z]*$")){
            return new ResponseEntity<>("Last name is not valid. The name can only contain letters", HttpStatus.FORBIDDEN);
        }
        if (clientRepository.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        }
        String rNumber=null;
        do{
            int randomNumber =  (int) (Math.random() * 10000000) + 1;
            rNumber="VIN"+String.valueOf(randomNumber);
        }while(accountRepository.findByNumber(rNumber)!=null);
        Client client=new Client(firstName, lastName, email, passwordEncoder.encode(password));
        clientRepository.save(client);
        Account account=new Account(rNumber, LocalDateTime.now() ,0.0);
        client.addAccount(account);
        accountRepository.save(account);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
