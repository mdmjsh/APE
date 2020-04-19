package com.oocode;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import static org.mockito.Mockito.*;

public class TestTideCalculator {
    @Test
    public void testInterpolateTideHeight() throws IOException {
        TideAPIAdapter tideAPIAdapter = mock(TideAPIAdapter.class);
        String knownErrorData = "LW 06:00 2.55\nHW 12:11 3.39\nLW 18:22 2.85";

        // Override the initTideAPIAdapter to plug in the mock Adapter
        TideCalculator tideCalculator = new TideCalculator(){

            @Override
            protected TideAPIAdapter initTideAPIAdapter(){
                return tideAPIAdapter;
            }
        };

        // return the example data known to have shown buggy behaviour
        when(tideAPIAdapter.getTideTimesString("Folkestone", "12-01-2020")).thenReturn(knownErrorData);
        assertEquals(tideCalculator.MidDayTide("Folkestone", "12-01-2020").toString(),
                "3.37");
    }
    // original attempt but couldn't get this working due to precision
        //        assertThat(tideCalculator.MidDayTide("Folkestone", "12-01-2020"),
//                Matchers.comparesEqualTo(new BigDecimal(3.37)));
//    }
}

