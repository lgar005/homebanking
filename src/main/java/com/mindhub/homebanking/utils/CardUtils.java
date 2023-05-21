package com.mindhub.homebanking.utils;


import com.mindhub.homebanking.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;

public final class CardUtils {
    @Autowired
   CardService cardService;

    private CardUtils() {

    }

    static public String getCardNumber(){
        double number=1000+ Math.random() * 9000;
        int numberC=(int)number;
        return  String.valueOf(numberC);
    }


    static public int getCvv(){
        double cvv = 100+ Math.random() * 900;
        int cvvC=(int)cvv;
        return cvvC;
    }
}
