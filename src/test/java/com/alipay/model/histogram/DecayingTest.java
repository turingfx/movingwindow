package com.alipay.model.histogram;

import org.junit.Test;

import java.time.Duration;

import static com.alipay.utils.DoubleUtil.round;
import static org.junit.Assert.assertEquals;

/**
 * Test for {@link DecayingHistogram}
 */
public class DecayingTest {
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
    public void testOption() {
        HistogramOptions  testOption2    = new HistogramOptions(500, 40, 1.5, epsilon);
        assertEquals(testOption2.getNumBuckets(), 6);
        assertEquals(testOption2.getBucketStart(0), 0, 0);
        assertEquals(testOption2.getBucketStart(1), 40, 0);
        assertEquals(testOption2.getBucketStart(2), 100, 0);
        assertEquals(testOption2.getBucketStart(3), 190, 0);
        assertEquals(testOption2.getBucketStart(4), 325, 0);
        assertEquals(testOption2.getBucketStart(5), 527.5, 0);

        assertEquals(testOption2.findBucket(-1), 0);
        assertEquals(testOption2.findBucket(39.99), 0);
        assertEquals(testOption2.findBucket(40), 1);
        assertEquals(testOption2.findBucket(100), 2);
        assertEquals(testOption2.findBucket(900), 5);
    }

    @Test
    public void testDecay() {
        reset();

        for (double p = -0.5; p < 100.5; p += 3.5) {
            assertEquals(decayingHistogram.percentile(p), 0, 0);
        }

        decayingHistogram.addSample(getGeometricSumOf2(2), 1000, startTime);
        decayingHistogram.addSample(getGeometricSumOf2(1), 1, startTime + Duration.ofHours(20).toMillis());
        System.out.println(decayingHistogram);
        assertEquals(decayingHistogram.percentile(0.999),getGeometricSumOf2(2),valueEpsilon);
        assertEquals(decayingHistogram.percentile(1), getGeometricSumOf2(3), valueEpsilon);
    }

    public static double getGeometric(double start, double ratio, int num) {
        return round(start * (1 - Math.pow(ratio, num)) / (1 - ratio));
    }

    public static double getGeometricSumOf2(int num) {
        return getGeometric(1, 2, num);
    }
}