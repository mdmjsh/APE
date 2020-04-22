package com.oocode;

import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;

public class TestQueryClock {

    @Test
    public void TestDaysFromToday() throws ParseException {
        QueryClock queryClock = new QueryClock(){
                @Override
                protected Date getCurrentDate() throws ParseException {
                    return QueryClock.formatter.parse("01-01-2020");
            }
        };
        assertEquals(queryClock.daysFromToday("01-01-2020"), 0);
        assertEquals(queryClock.daysFromToday("10-01-2020"), 9);
        assertEquals(queryClock.daysFromToday("11-01-2020"), 10);
        assertEquals(queryClock.daysFromToday("12-01-2020"), 11);
    }
}
