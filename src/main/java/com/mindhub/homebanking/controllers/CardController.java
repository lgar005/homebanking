package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
public class CardController {
    @Autowired
    private ClientService clientService;

    @Autowired
    private CardService cardService;

    @PostMapping("/api/clients/current/cards")
    public ResponseEntity<Object> createCard(Authentication authentication, @RequestParam CardType type, @RequestParam CardColor color){
        Client client=clientService.getAuthenticatedClient(authentication);
        ResponseEntity<Object> response=new ResponseEntity<>("undefined data", HttpStatus.FORBIDDEN);
        if(client!=null){
            if(client.getCards().stream().filter(card -> card.getType()==type && card.getColor()==color && card.isActive()).collect(Collectors.toList()).size()==0){
                Card card= new Card(client.getFirstName()+" "+client.getLastName(),type,color,cardService.numberCardNotRepeat(),CardUtils.getCvv(), LocalDateTime.now(), LocalDateTime.now().plusYears(5));
                client.addCard(card);
                cardService.saveCard(card);
                response=new ResponseEntity<>(HttpStatus.CREATED);
            } else if (client.getCards().stream().filter(card -> card.getType()==type && card.getColor()==color && card.isActive()).collect(Collectors.toList()).size()!=0) {
                response=new ResponseEntity<>("You have a card with these characteristics. Acquire a different card.", HttpStatus.FORBIDDEN);
            }
            return response;
        }else{
            return new ResponseEntity<>("Client not found", HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping(path = "/api/clients/current/cards")
    public ResponseEntity<Object> deleteCard(Authentication authentication, @RequestParam String cardNumber){
        Client client= clientService.getAuthenticatedClient(authentication);
        Card card = cardService.findByNumber(cardNumber);
        if(card==null){
            return new ResponseEntity<>("The card does not exist.", HttpStatus.FORBIDDEN);
        }if(card.getClient()!=client){
            return new ResponseEntity<>("Please verify the information, this card does not belong to you.", HttpStatus.FORBIDDEN);
        }else{
            card.setActive(false);
            cardService.saveCard(card);
            return new ResponseEntity<>("The card has been deleted", HttpStatus.OK);
        }
    }

}
