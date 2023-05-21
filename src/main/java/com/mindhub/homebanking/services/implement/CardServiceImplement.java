package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImplement implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Override
    public Card findByNumber(String number) {
        return cardRepository.findByNumber(number);
    }

    @Override
    public void saveCard(Card card) {
        cardRepository.save(card);
    }

    @Override
    public String numberCardNotRepeat() {
        String numberCard;
        do {
            numberCard =CardUtils.getCardNumber()+"-"+CardUtils.getCardNumber()+"-"+CardUtils.getCardNumber()+"-"+CardUtils.getCardNumber();
        } while(cardRepository.findByNumber(numberCard) != null);
        return numberCard;
    }
}
