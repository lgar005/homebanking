package com.mindhub.homebanking.utils;

public final class AccountUtils {

    public AccountUtils() {
    }
    static public String getAccountNumber(){
        int randomNumber =  (int) (Math.random() * 100000) + 1;
        String rNumber="VIN-"+randomNumber;
        return rNumber;
    }
}
