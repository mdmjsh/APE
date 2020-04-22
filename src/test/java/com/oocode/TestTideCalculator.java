package com.oocode;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.ParseException;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;


public class TestTideCalculator {
    static final String walkThroughData = "HW 03:57 3.90\nLW 10:09 1.40\nHW 16:21 4.00\nLW 22:33 1.60";
    static final String knownErrorData = "LW 06:00 2.55\nHW 12:11 3.39\nLW 18:22 2.85";
    private final TideCalculator.TideTimeHeight knownErrorLowTide = TideAPIAdapter.getTideTimeHeight(
            knownErrorData.split("\n")[0]);
    private final TideCalculator.TideTimeHeight knownErrorHighTide = TideAPIAdapter.getTideTimeHeight(
            knownErrorData.split("\n")[1]);
    private final TideCalculator.TideTimeHeight[] knownErrorDataTides = new TideCalculator.TideTimeHeight[]{
            knownErrorLowTide, knownErrorHighTide};

    private final TideCalculator.TideTimeHeight walkThroughLowTide = TideAPIAdapter.getTideTimeHeight(
            walkThroughData.split("\n")[1]);
    private final TideCalculator.TideTimeHeight walkThroughHighTide = TideAPIAdapter.getTideTimeHeight(
            walkThroughData.split("\n")[2]);
    private final TideCalculator.TideTimeHeight[] walkThroughDataTides = new TideCalculator.TideTimeHeight[]{
            walkThroughLowTide, walkThroughHighTide};

    @Test
    public void testInterpolateTideHeight() throws IOException {
        TideAPIAdapter tideAPIAdapter = mock(TideAPIAdapter.class);
        // Override the initTideAPIAdapter to plug in the mock Adapter
        TideCalculator tideCalculator;
        tideCalculator = new TideCalculator(){
            @Override
            protected TideAPIAdapter initTideAPIAdapter(){
                return tideAPIAdapter;
            }
        };

        // return the example data known to have shown buggy behaviour
        when(tideAPIAdapter.getTideTimesString("Folkestone", "12-01-2020")).thenReturn(knownErrorData);
        when(tideAPIAdapter.getLowAndHighTides(knownErrorData)).thenReturn(knownErrorDataTides);
        assertEquals(tideCalculator.MidDayTide("Folkestone", "12-01-2020").toString(),
                "3.37");

        // run again with the example data
        when(tideAPIAdapter.getTideTimesString("Folkestone", "12-01-2020")).thenReturn(walkThroughData);
        when(tideAPIAdapter.getLowAndHighTides(walkThroughData)).thenReturn(walkThroughDataTides);
        assertEquals(tideCalculator.MidDayTide("Folkestone", "12-01-2020").toString(),
                "2.18");
    }

    @Test
    public void testIsWithinWindow() throws ParseException {
        QueryClock queryClock = mock(QueryClock.class);
        // Override the queryClock attribute in the TideAdapter
        TideCalculator tideCalculator = new TideCalculator(){
            @Override
            protected QueryClock initQueryClock(){
                return queryClock;
            }
        };

        for (int daysFromToday : new int[]{1,9,10,11}) {
            when(queryClock.DaysFromToday("12-01-2020")).thenReturn(daysFromToday);
            assertEquals(tideCalculator.isWithinWindow("12-01-2020"), daysFromToday <=10 );
        }
    }

//    @Test
//    public  void test
    // call count = 0
    // Overload API - increment call count
    // Overload isWithinWindow (return True / False)
    // assert call count

    @Test
    public void TestCallsWhenWithinWindow() throws IOException {
        PrintStream mockPrinter = mock(PrintStream.class);
        System.setOut(mockPrinter);

        // slightly hacky solution to incrementing the counts from the inner class
        final int[] isWithinWindowCalls = {0};
        final int[] getLowAndHighTides = {0};
        final int[] getTideTimeStringCalls = {0};
        final int[] interpolateTideHeightCalls = {0};

        // Override all methods we don't need to actually call in this test

        TideCalculator tideCalculator = new TideCalculator(){

            protected boolean isWithinWindow(){
                isWithinWindowCalls[0]++;
                return true;
            }

            protected TideTimeHeight[] getLowAndHighTides(){
                getLowAndHighTides[0]++;
                return walkThroughDataTides;
            }

            protected String getTideTimesString(){
                getTideTimeStringCalls[0]++;
                return  "blah";
            }

            protected BigDecimal interpolateTideHeight(){
                interpolateTideHeightCalls[0]++;
                return BigDecimal.ONE;
            }
        };
        tideCalculator.MidDayTide("Folkestone", "12-01-2020");
        // Get the first element of the single element call count arrays from the inner classes
        int[] calls = {isWithinWindowCalls[0], getLowAndHighTides[0],
                getTideTimeStringCalls[0], interpolateTideHeightCalls[0]};
        for (int count: calls){
            // When isWithinWindowCalls is True all calls are made
            assertEquals(count, 1);
        }



        verify(mockPrinter).println(startsWith("Use:"));
    }
}

