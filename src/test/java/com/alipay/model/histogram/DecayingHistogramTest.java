package com.alipay.model.histogram;

import org.junit.Test;

import static org.junit.Assert.*;

public class DecayingHistogramTest {
    @Test
    public void testRoundWithHalfLife() {
        long a1 = 1200;
        long a2 = 500;
        long rs = Math.round((double) a1 / a2) * a2;
        assert rs == 1000;
    }
}