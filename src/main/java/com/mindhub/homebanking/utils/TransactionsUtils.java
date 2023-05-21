package com.mindhub.homebanking.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

public final class TransactionsUtils {

    public static Date stringtoDate (String string) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(string);
    }

    public static LocalDateTime dateToLocalDateTime (Date date){
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static LocalDateTime formatDate(LocalDateTime dateCreated){
        DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        String date1= fmt.format(dateCreated);
        LocalDateTime date1f=LocalDateTime.parse(date1,fmt);
        return  date1f;
    }
}
