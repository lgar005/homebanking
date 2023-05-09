package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.ClientService;
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
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientService clientService;

    @RequestMapping("/api/clients")
    public List<ClientDTO> getClients(){
        return clientService.getClientsDTO();
    }
    @RequestMapping("/api/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        //clientRepository.findById(id).map(client -> new ClientDTO(client)).orElse(null);
        return  clientService.getClientDTO(id);

    }

    @RequestMapping("/api/clients/current")
    public ClientDTO getClient(Authentication authentication) {
       return clientService.getClientDTO(authentication);
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
        if (clientService.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        }
        String rNumber=null;
        do{
            int randomNumber =  (int) (Math.random() * 10000000) + 1;
            rNumber="VIN"+String.valueOf(randomNumber);
        }while(accountRepository.findByNumber(rNumber)!=null);
        Client client=new Client(firstName, lastName, email, passwordEncoder.encode(password));
        //clientRepository.save(client);
        clientService.saveClient(client);
        Account account=new Account(rNumber, LocalDateTime.now() ,0.0);
        client.addAccount(account);
        accountRepository.save(account);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
