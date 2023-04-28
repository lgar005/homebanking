package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
public class CardController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CardRepository cardRepository;
    @PostMapping("/api/clients/current/cards")
    public ResponseEntity<Object> createCard(Authentication authentication, @RequestParam CardType type, @RequestParam CardColor color){
        Client client=clientRepository.findByEmail(authentication.getName());
        ResponseEntity<Object> response=new ResponseEntity<>("undefined data", HttpStatus.FORBIDDEN);
        if(client!=null){
            if(client.getCards().stream().filter(card -> card.getType()==type && card.getColor()==color).collect(Collectors.toList()).size()==0){
                int cvvC;
                do {
                     double cvv = 100+ Math.random() * 900;
                     cvvC=(int)cvv;
                }while (cardRepository.findByCvv(cvvC)!=null);

                String number="";
                do{
                     number=generateNumber()+"-"+generateNumber()+"-"+generateNumber()+"-"+generateNumber();
                }while (cardRepository.findByNumber(number)!=null);
                Card card= new Card(client.getFirstName()+" "+client.getLastName(),type,color,number,cvvC, LocalDateTime.now(), LocalDateTime.now().plusYears(5));
                client.addCard(card);
                cardRepository.save(card);
                response=new ResponseEntity<>(HttpStatus.CREATED);
            } else if (client.getCards().stream().filter(card -> card.getType()==type && card.getColor()==color).collect(Collectors.toList()).size()!=0) {
                response=new ResponseEntity<>("You have a card with these characteristics. Acquire a different card.", HttpStatus.FORBIDDEN);
            }
            return response;
        }else{
            return new ResponseEntity<>("Client not found", HttpStatus.FORBIDDEN);
        }
    }

    public String generateNumber(){
        double number=1000+ Math.random() * 9000;
        int numberC=(int)number;
        return  String.valueOf(numberC);
    }
}
