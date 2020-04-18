package com.oocode;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class TestTideCalculator {
    @Test
    public void testInterpolateTideHeight {
        mockMakeApiCall = mock(TideCalculator.makeApiCall())

        assertThat(, equalTo(1));
    }
}
