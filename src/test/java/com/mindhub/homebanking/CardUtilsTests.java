package com.mindhub.homebanking;

import com.mindhub.homebanking.utils.CardUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class CardUtilsTests {
    @Test
    public void cardNumberIsCreated(){
        String cardNumber = CardUtils.getCardNumber();
        assertThat(cardNumber,is(not(emptyOrNullString())));
    }

    @Test
    public void cardNumberLenght(){
        String number=CardUtils.getCardNumber()+"-"+CardUtils.getCardNumber()+"-"+CardUtils.getCardNumber()+"-"+CardUtils.getCardNumber();
        assertThat(number,hasLength(19));
    }

    @Test
    public void validateFourDigitCardValue(){
        String number=CardUtils.getCardNumber();
        int numberI= Integer.parseInt(number);
        assertThat(numberI,is(greaterThanOrEqualTo(1000)));
        assertThat(numberI,is(lessThanOrEqualTo(9990)));
    }

    @Test
    public void cardCvvLenght(){
        int cvv=  CardUtils.getCvv();
        String cvvString= String.valueOf(cvv);
        assertThat(cvvString,hasLength(3));
    }
}
