package com.alipay.model.histogram;

import com.alipay.histogram.DecayingHistogram;
import com.alipay.histogram.HistogramOptions;
import org.junit.Test;

import java.time.Duration;

import static com.alipay.model.histogram.HistogramTest.getGeometricSumOf2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link DecayingHistogram}
 */
public class DecayingHistogramTest {
    public static final long hour_1 = Duration.ofHours(1).toMillis();

    double            epsilon       = 0.001;
    Long              startTime     = 1234567890L; // any time is ok
    double            valueEpsilon  = 1e-15;
    double            weightEpsilon = 1e-15;
    HistogramOptions  testOption    = new HistogramOptions(1000, 1, 2, epsilon);
    DecayingHistogram decayingHistogram;

    public void reset() {
        decayingHistogram = new DecayingHistogram(testOption, hour_1);
    }

    @Test
    public void testSimpleDecay() {
        reset();
        decayingHistogram.addSample(getGeometricSumOf2(2), 1000, startTime);
        decayingHistogram.addSample(getGeometricSumOf2(1), 1, startTime + Duration.ofHours(20).toMillis());

        assertEquals(decayingHistogram.percentile(0.999), getGeometricSumOf2(2), valueEpsilon);
        assertEquals(decayingHistogram.percentile(1), getGeometricSumOf2(3), valueEpsilon);
    }

    @Test
    public void testLongDecay() {
        reset();
        decayingHistogram.addSample(getGeometricSumOf2(2), 1, startTime);
        decayingHistogram.addSample(getGeometricSumOf2(1), 1, startTime + Duration.ofHours(101).toMillis());
        assertEquals(decayingHistogram.percentile(1), getGeometricSumOf2(2), valueEpsilon);
    }

    @Test
    public void testNoDecay() {
        reset();

        for (int i = 0; i <= 4; i++) {
            double geometricSumOf2 = getGeometricSumOf2(i);
            decayingHistogram.addSample(geometricSumOf2, i, startTime);
        }

        assertEquals(3, decayingHistogram.percentile(0), 0.0);
        assertEquals(3, decayingHistogram.percentile(0.1), 0.0);
        assertEquals(7, decayingHistogram.percentile(0.2), 0.0);
        assertEquals(7, decayingHistogram.percentile(0.3), 0.0);
        assertEquals(15, decayingHistogram.percentile(0.4), 0.0);
        assertEquals(15, decayingHistogram.percentile(0.5), 0.0);
        assertEquals(15, decayingHistogram.percentile(0.6), 0.0);
        assertEquals(31, decayingHistogram.percentile(0.7), 0.0);
        assertEquals(31, decayingHistogram.percentile(0.8), 0.0);
        assertEquals(31, decayingHistogram.percentile(0.9), 0.0);
        assertEquals(31, decayingHistogram.percentile(1), 0.0);
    }

    @Test
    public void testDecayingPercentile() {
        reset();

        Long ts = startTime;
        for (int i = 1; i <= 4; i++) {
            decayingHistogram.addSample(getGeometricSumOf2(i), i, ts);
            ts += Duration.ofHours(1).toMillis();
        }

        assertEquals(getGeometricSumOf2(2), decayingHistogram.percentile(0), 0.0);
        assertEquals(getGeometricSumOf2(2), decayingHistogram.percentile(0.02), 0.0);
        assertEquals(getGeometricSumOf2(3), decayingHistogram.percentile(0.03), 0.0);
        assertEquals(getGeometricSumOf2(3), decayingHistogram.percentile(0.1), 0.0);
        assertEquals(getGeometricSumOf2(4), decayingHistogram.percentile(0.11), 0.0);
        assertEquals(getGeometricSumOf2(4), decayingHistogram.percentile(0.34), 0.0);
        assertEquals(getGeometricSumOf2(5), decayingHistogram.percentile(0.35), 0.0);
        assertEquals(getGeometricSumOf2(5), decayingHistogram.percentile(1), 0.0);
    }

    @Test
    public void testIsVarDramatic() {
        reset();
        double p = 0.9;

        decayingHistogram.addSample(0.1,1,0L);
        assertFalse(decayingHistogram.isVaryDramatic(p));

        decayingHistogram.addSample(2,1,0L);
        assertTrue(decayingHistogram.isVaryDramatic(p));

        decayingHistogram.addSample(3,90,0L);
        assertFalse(decayingHistogram.isVaryDramatic(p));
    }
}