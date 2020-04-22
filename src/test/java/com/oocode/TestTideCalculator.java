package com.oocode;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
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
            when(queryClock.daysFromToday("12-01-2020")).thenReturn(daysFromToday);
            assertEquals(tideCalculator.isWithinWindow("12-01-2020"), daysFromToday <=10 );
        }
    }


    @Test
    public void TestMainWhenWithinWindow() throws Exception {
        // slightly hacky solution to incrementing the counts from the inner class
        final int[] MidDayTideCalls = {0};

        // Override all methods we don't need to actually call in this test
        TideCalculator tideCalculator = new TideCalculator(){

            @Override
            protected boolean isWithinWindow(String date){
                return true;
            }

            @Override
            protected BigDecimal MidDayTide(String place, String date){
                MidDayTideCalls[0]++;
                return BigDecimal.ONE;
            }
        };
        tideCalculator.main(new String[]{"Folkestone", "12-01-2020"});
        // Get the first element of the single element call count arrays from the inner classes
        assertEquals(MidDayTideCalls[0], 1);
    }
    /** Very similar test to the above, but testing the printer is called when ourside of window */
    @Test
    public void TestMainPrintWhenOutsideWindow() throws Exception {
        PrintStream mockPrinter = mock(PrintStream.class);
        System.setOut(mockPrinter);

        // slightly hacky solution to incrementing the counts from the inner class
        final int[] MidDayTideCalls = {0};

        // Override all methods we don't need to actually call in this test
        TideCalculator tideCalculator = new TideCalculator(){

            @Override
            protected boolean isWithinWindow(String date){
                return false;
            }

            @Override
            protected BigDecimal MidDayTide(String place, String date){
                MidDayTideCalls[0]++;
                return BigDecimal.ONE;
            }
        };
        tideCalculator.main(new String[]{"Folkestone", "12-01-2020"});
        // Get the first element of the single element call count arrays from the inner classes
        assertEquals(MidDayTideCalls[0], 0);
        verify(mockPrinter).println(startsWith(tideCalculator.OUTSIDE_WINDOW));
    }

}

