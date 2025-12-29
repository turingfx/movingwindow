package com.alipay.model.histogram;

import org.junit.Test;

/**
 * Test for {@link Histogram} and {@link HistogramOptions}
 */
public class HistogramTest {

    float            epsilon       = 0.001f;
    float            valueEpsilon  = 1e-15f;
    float            weightEpsilon = 1e-15f;
    HistogramOptions testOption    = new HistogramOptions(1000f, 1, 2f, epsilon);

    @Test
    public void testOptionSetUp() {
        HistogramOptions setUpOption = new HistogramOptions(500f, 40f, 1.5f, epsilon);
        assert setUpOption.getEpsilon() == epsilon;
        assert setUpOption.getNumBuckets() == 6;

        assert setUpOption.getBucketStart(0) == 0f;
        assert setUpOption.getBucketStart(1) == 40f;
        assert setUpOption.getBucketStart(2) == 100f;
        assert setUpOption.getBucketStart(3) == 190f;
        assert setUpOption.getBucketStart(4) == 325f;
        assert setUpOption.getBucketStart(5) == 527.5f;

        assert setUpOption.findBucket(-1) == 0;
        assert setUpOption.findBucket(39.99f) == 0;
        assert setUpOption.findBucket(40f) == 1;
        assert setUpOption.findBucket(100f) == 2;
        assert setUpOption.findBucket(900f) == 5;
    }

    @Test
    public void testPercentile() {
        Histogram testGram = new Histogram(testOption);

        for (float p = -0.5f; p <= 100.5; p += 3.5f) {
            // when gram is empty, percentile should be 0
            assert testGram.percentile(p) == 0;
        }

        for (int i = 0; i <= 4; i++) {
            float value = getGeometricSumOf2(i);
            testGram.addSample(value, i, null);
        }
        //System.out.println(testGram);

        assert testGram.percentile(0) == 3;
        assert testGram.percentile(0.1f) == 3;
        assert testGram.percentile(0.2f) == 7;
        assert testGram.percentile(0.3f) == 7;
        assert testGram.percentile(0.4f) == 15;
        assert testGram.percentile(0.5f) == 15;
        assert testGram.percentile(0.6f) == 15;
        assert testGram.percentile(0.7f) == 31;
        assert testGram.percentile(0.8f) == 31;
        assert testGram.percentile(0.9f) == 31;
        assert testGram.percentile(1.0f) == 31;
    }

    @Test
    public void testPercentileOutBound() {
        Histogram testGram = new Histogram(testOption);

        testGram.addSample(getGeometricSumOf2(2), 0.1f, null);
        testGram.addSample(getGeometricSumOf2(3), 0.2f, null);

        assert testGram.percentile(-0.1f) == 3;
        assert testGram.percentile(1.1f) == 4;
    }

    @Test
    public void testEmpty() {
        Histogram testGram = new Histogram(testOption);

        assert testGram.isEmpty();

        testGram.addSample(0.1f,epsilon * 2.5f,null);
        assert !testGram.isEmpty();

        testGram.subtractSample(0.1f,epsilon,null);
        assert !testGram.isEmpty();

        testGram.subtractSample(0.1f,epsilon,null);
        assert testGram.isEmpty();
    }

    @Test
    public void testMax() {
        Histogram testGram = new Histogram(testOption);
        assert testGram.max() == 0;

        testGram.addSample(0.1f,0.1f,null);
        assert testGram.max()== 1;

        testGram.addSample(100,0.1f,null);
        assert testGram.max() == testGram.percentile(1f);
        assert testGram.max() == 127;
    }

    @Test
    public void testAverage() {
        Histogram testGram = new Histogram(testOption);
        assert testGram.average() == 0;

        testGram.addSample(0.1f,0.1f,null);
        assert testGram.average() == 1;

        testGram.addSample(2,0.9f,null);
        System.out.println(testGram);
        // (3*0.9+1*0.1)/(0.1+0.9)
        assert testGram.average() == 2.8;
    }

    @Test
    public void testMaxBucketWeight() {
        Histogram testGram = new Histogram(testOption);
        assert testGram.maxBucketWeight() == 0;
        testGram.addSample(0.1f,0.1f,null);
        assert testGram.maxBucketWeight() == 0.1;
        testGram.addSample(2f,0.9f,null);
        assert testGram.maxBucketWeight() == 0.9;
        testGram.addSample(4f,0.7f,null);
        assert testGram.maxBucketWeight() == 0.9;
        testGram.addSample(4f,0.4f,null);
        assert testGram.maxBucketWeight() == 1.1;
    }

    @Test
    public void testMathMethod() {
        long a1 = 1200;
        long a2 = 500;
        assert Math.round((double) a1 / a2) * a2 == 1000;

        float a3 = 1.5f;
        assert Math.ceil(a3) == 2;
    }

    private float getGeometric(float start, float ratio, int num) {
        return (float) (start * (1 - Math.pow(ratio, num)) / (1 - ratio));
    }

    private float getGeometricSumOf2(int num) {
        return getGeometric(1, 2, num);
    }
}