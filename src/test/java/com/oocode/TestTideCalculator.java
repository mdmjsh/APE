package com.oocode;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class TestTideCalculator {
    @Test
    public void testInterpolateTideHeight() throws IOException {
        TideAPIAdapter tideAPIAdapter = mock(TideAPIAdapter.class);
        String knownErrorData = "LW 06:00 2.55\nHW 12:11 3.39\nLW 18:22 2.85";
        TideCalculator tideCalculator = new TideCalculator();
        // return the example data known to have shown buggy behaviour

        class TideCalculator{}


        when(tideAPIAdapter.getTideTimesString("Folkestone", "12-01-2020")).thenReturn(knownErrorData);
        assertThat(tideCalculator, equalTo(3.36));
    }
}

