package com.oocode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class QueryClock implements  QueryClockInterface{
     static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    public int daysFromToday(String date) throws ParseException {
        Instant queryDate = QueryClock.formatter.parse(date).toInstant();
        Instant today = GetCurrentDate().toInstant();
        return (int) Duration.between(today, queryDate).toDays();
    }
    Date GetCurrentDate() throws ParseException {
      return new Date();
    };

}
