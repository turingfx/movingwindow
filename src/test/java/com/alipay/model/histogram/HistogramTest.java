package com.alipay.model.histogram;

import org.junit.Test;

import static com.alipay.utils.DoubleUtil.round;
import static org.junit.Assert.*;

/**
 * Test for {@link Histogram} and {@link HistogramOptions}
 */
public class HistogramTest {

    double            epsilon       = 0.001;
    double            valueEpsilon  = 1e-15;
    double            weightEpsilon = 1e-15;
    HistogramOptions testOption    = new HistogramOptions(1000, 1, 2, epsilon);

    @Test
    public void testOptionSetUp() {
        HistogramOptions setUpOption = new HistogramOptions(500, 40, 1.5, epsilon);
        assertEquals(epsilon, setUpOption.getEpsilon(), 0.0);
        assertEquals(6, setUpOption.getNumBuckets());

        assertEquals(0, setUpOption.getBucketStart(0), 0.0);
        assertEquals(40, setUpOption.getBucketStart(1), 0.0);
        assertEquals(100, setUpOption.getBucketStart(2), 0.0);
        assertEquals(190, setUpOption.getBucketStart(3), 0.0);
        assertEquals(325, setUpOption.getBucketStart(4), 0.0);
        assertEquals(527.5, setUpOption.getBucketStart(5), 0.0);

        assertEquals(0, setUpOption.findBucket(-1));
        assertEquals(0, setUpOption.findBucket(39.99));
        assertEquals(1, setUpOption.findBucket(40));
        assertEquals(2, setUpOption.findBucket(100));
        assertEquals(5, setUpOption.findBucket(900));
    }

    @Test
    public void testPercentile() {
        Histogram testGram = new Histogram(testOption);

        for (double p = -0.5; p <= 100.5; p += 3.5) {
            // when gram is empty, percentile should be 0
            assertEquals(0, testGram.percentile(p), 0.0);
        }

        for (int i = 0; i <= 4; i++) {
            double value = getGeometricSumOf2(i);
            testGram.addSample(value, i, null);
        }
        //System.out.println(testGram);

        assertEquals(3, testGram.percentile(0), 0.0);
        assertEquals(3, testGram.percentile(0.1), 0.0);
        assertEquals(7, testGram.percentile(0.2), 0.0);
        assertEquals(7, testGram.percentile(0.3), 0.0);
        assertEquals(15, testGram.percentile(0.4), 0.0);
        assertEquals(15, testGram.percentile(0.5), 0.0);
        assertEquals(15, testGram.percentile(0.6), 0.0);
        assertEquals(31, testGram.percentile(0.7), 0.0);
        assertEquals(31, testGram.percentile(0.8), 0.0);
        assertEquals(31, testGram.percentile(0.9), 0.0);
        assertEquals(31, testGram.percentile(1.0), 0.0);
    }

    @Test
    public void testPercentileOutBound() {
        Histogram testGram = new Histogram(testOption);

        testGram.addSample(getGeometricSumOf2(2), 0.1, null);
        testGram.addSample(getGeometricSumOf2(3), 0.2, null);

        assertEquals(getGeometricSumOf2(3), testGram.percentile(-0.1), 0.0);
        assertEquals(getGeometricSumOf2(4), testGram.percentile(1.1), 0.0);

        testGram.addSample(0.5,0.1,null);
        testGram.addSample(getGeometricSumOf2(4),0.2,null);

        assertEquals(getGeometricSumOf2(1), testGram.percentile(-0.1), 0.0);
        assertEquals(getGeometricSumOf2(5), testGram.percentile(1.1), 0.0);
    }

    @Test
    public void testClusterAtExtremes() {
        Histogram testGram = new Histogram(testOption);
        for (int i = 0; i < 9; i++){
            testGram.addSample(1,1,null);
        }
        assertFalse(testGram.clusterAtTheExtremes(0.1));

        testGram.addSample(10000,1,null);
        assertTrue(testGram.clusterAtTheExtremes(0.1));

        testGram.addSample(1,1,null);
        assertFalse(testGram.clusterAtTheExtremes(0.1));

    }

    @Test
    public void testEmpty() {
        Histogram testGram = new Histogram(testOption);

        assertTrue(testGram.isEmpty());

        testGram.addSample(0.1,epsilon * 2.5,null);
        assertFalse(testGram.isEmpty());

        testGram.subtractSample(0.1,epsilon,null);
        assertFalse(testGram.isEmpty());

        testGram.subtractSample(0.1,epsilon,null);
        assertTrue(testGram.isEmpty());
    }

    @Test
    public void testMax() {
        Histogram testGram = new Histogram(testOption);
        assertEquals(0, testGram.max(), 0.0);

        testGram.addSample(0.1,0.1,null);
        assertEquals(1, testGram.max(), 0.0);

        testGram.addSample(100,0.1,null);
        assertEquals(testGram.percentile(1), testGram.max(), 0.0);
        assertEquals(127, testGram.max(), 0.0);
    }

    @Test
    public void testAverage() {
        Histogram testGram = new Histogram(testOption);
        assertEquals(0, testGram.average(), 0.0);

        testGram.addSample(0.1,0.1,null);
        assertEquals(1, testGram.average(), 0.0);

        testGram.addSample(2,0.9,null);
        // (3*0.9+1*0.1)/(0.1+0.9)
        assertEquals(2.8, testGram.average(), 0.0);
    }

    @Test
    public void testMaxBucketWeight() {
        Histogram testGram = new Histogram(testOption);
        assertEquals(0, testGram.maxBucketWeight(), 0.0);
        testGram.addSample(0.1,0.1,null);
        assertEquals(0.1, testGram.maxBucketWeight(), 0.0);
        testGram.addSample(2,0.9,null);
        assertEquals(0.9, testGram.maxBucketWeight(), 0.0);
        testGram.addSample(4,0.7,null);
        assertEquals(0.9, testGram.maxBucketWeight(), 0.0);
        testGram.addSample(4,0.4,null);
        assertEquals(1.1, testGram.maxBucketWeight(), 0.0);
    }

    @Test
    public void testMathMethod() {
        long a1 = 1200;
        long a2 = 500;
        assertEquals(1000, Math.round((double) a1 / a2) * a2);
    }

    public static double getGeometric(double start, double ratio, int num) {
        return round(start * (1 - Math.pow(ratio, num)) / (1 - ratio));
    }

    public static double getGeometricSumOf2(int num) {
        return getGeometric(1, 2, num);
    }
}