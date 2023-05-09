package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;

public interface CardService {

    Card findByNumber(String number);

    void saveCard(Card card);

}
