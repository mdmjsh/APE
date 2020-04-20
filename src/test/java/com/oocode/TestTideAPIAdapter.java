package com.oocode;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


public class TestTideAPIAdapter {

    @Test
    public void testGetLowAndHighTides(){
        assertEquals(TideAPIAdapter.getFirstLowTideIndex(TestTideCalculator.knownErrorData), 0);
        assertEquals(TideAPIAdapter.getFirstLowTideIndex(TestTideCalculator.walkThroughData), 1);
    }
}

