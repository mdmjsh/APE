package com.oocode;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
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


        // run again with the example data
        String walkThroughData = "HW 03:57 3.90\nLW 10:09 1.40\nHW 16:21 4.00\nLW 22:33 1.60";
        when(tideAPIAdapter.getTideTimesString("Folkestone", "12-01-2020")).thenReturn(walkThroughData);
        assertEquals(tideCalculator.MidDayTide("Folkestone", "12-01-2020").toString(),
                "2.18");
    }



    @Test
    public void testGetLowAndHighTides
}

