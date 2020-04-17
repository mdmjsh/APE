package com.oocode;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SomeTest {
    @Test
    public void assignment_eg1() {
        assertThat(1, equalTo(1));
    }

}
