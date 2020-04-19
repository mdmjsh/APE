package com.oocode;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class TestTideCalculator {
    @Test
    public void testInterpolateTideHeight() throws IOException {
        TideCalculator tideCalculator = mock(TideCalculator.class);
        String knownErrorData = "LW 06:00 2.55\nHW 12:11 3.39\nLW 18:22 2.85";

        when(tideCalculator.getTideTimesString("Folkestone", "12-01-2020")).thenReturn(knownErrorData);
        assertThat(tideCalculator, equalTo(3.36));
    }
}

