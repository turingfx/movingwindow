package com.alipay.model.histogram;

import com.alipay.utils.DoubleUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DoubleUtilTest {

    @Test
    public void addTest() {
        double a = 1.,b=2.,c=3.1,d=4.2345,e=5.0001,f=6.00001;
        assertTrue(DoubleUtil.equal(3., DoubleUtil.add(a,b)));
        assertTrue(DoubleUtil.equal(7.3345,DoubleUtil.add(c,d)));
        assertTrue(DoubleUtil.equal(9.2346,DoubleUtil.add(d,e)));
        // lost precision when exceed 4
        assertTrue(DoubleUtil.equal(11.0001,DoubleUtil.add(e,f)));

        // assert equal
        assertEquals(11.0001,DoubleUtil.add(e,f),0);
        assertEquals(9.2346,DoubleUtil.add(d,e),0);
    }

    @Test
    public void subtractTest() {
        double a = 1.,b=2.,c=3.1,d=4.0001,e=5.00001;

        assertTrue(DoubleUtil.equal(1., DoubleUtil.subtract(b,a)));
        assertTrue(DoubleUtil.equal(-1., DoubleUtil.subtract(a,b)));
        assertTrue(DoubleUtil.equal(1.1, DoubleUtil.subtract(c,b)));
        assertTrue(DoubleUtil.equal(0.9001, DoubleUtil.subtract(d,c)));
        assertTrue(DoubleUtil.equal(0.9999, DoubleUtil.subtract(e,d)));

        assertEquals(0.9999, DoubleUtil.subtract(e,d),0);
    }

    @Test
    public void multiplyTest() {
        double a = 1.,b=2.,c=3.1,d=4.0001,e=5.00001;

        assertTrue(DoubleUtil.equal(2., DoubleUtil.multiply(a,b)));
        assertTrue(DoubleUtil.equal(6.2, DoubleUtil.multiply(c, b)));
        assertTrue(DoubleUtil.equal(12.4003, DoubleUtil.multiply(c,d)));
        assertTrue(DoubleUtil.equal(12.4003, DoubleUtil.multiply(c,d)));
        assertTrue(DoubleUtil.equal(20.0005, DoubleUtil.multiply(d,e)));
    }

    @Test
    public void divideTest() {
        double a = 1.,b=2.,c=3.1,d=4.0001,e=5.00001;

        assertTrue(DoubleUtil.equal(2., DoubleUtil.divide(b,a)));
        assertTrue(DoubleUtil.equal(0.5, DoubleUtil.divide(a, b)));
        assertTrue(DoubleUtil.equal(0.6452, DoubleUtil.divide(b, c)));
        assertTrue(DoubleUtil.equal(1.55, DoubleUtil.divide(c, b)));
        assertTrue(DoubleUtil.equal(2.0001, DoubleUtil.divide(d, b)));
        assertTrue(DoubleUtil.equal(2.5, DoubleUtil.divide(e, b)));
    }

    @Test
    public void roundTest() {
        double a = 1.,b=2.123,d=4.1111,e=5.00001;
        assertTrue(DoubleUtil.equal(1., DoubleUtil.round(a)));
        assertTrue(DoubleUtil.equal(2.123, DoubleUtil.round(b)));
        assertTrue(DoubleUtil.equal(4.1111, DoubleUtil.round(d)));
        assertTrue(DoubleUtil.equal(5., DoubleUtil.round(e)));
    }
}
