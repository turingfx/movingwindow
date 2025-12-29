package com.alipay.model.histogram;

import static com.alipay.consts.HistogramConsts.defaultEpsilon;
import static com.alipay.consts.HistogramConsts.defaultHistogramBucketSizeGrowth;

/**
 * @author sansi.xy
 * @date 2025/12/25
 */

public class HistogramOptions {
    private int   numBuckets;
    private float firstBucketSize;
    private float ratio;
    private float epsilon;

    public HistogramOptions(float maxValue, float firstBucketSize, float ratio, float epsilon) {
        if (maxValue <= 0 || firstBucketSize <= 0f || ratio <= 1f || epsilon <= 0f) {
            throw new IllegalArgumentException("invalid histogram options");
        }

        this.numBuckets = (int) (Math.ceil(logOverBase(ratio, maxValue * (ratio - 1) / firstBucketSize + 1)) + 1);
        this.firstBucketSize = firstBucketSize;
        this.ratio = ratio;
        this.epsilon = epsilon;
    }

    public HistogramOptions getCpuHistogramOptions() {
        // ceil is cu,cpu range is 0.01 ~ 1000
        return new HistogramOptions(1000, 0.01f, 1 + defaultHistogramBucketSizeGrowth, defaultEpsilon);
    }

    public HistogramOptions getMemHistogramOptions() {
        // ceil is B,mem range is 10MB ~ 1TB
        return new HistogramOptions(1e12f, 1e7f, 1 + defaultHistogramBucketSizeGrowth, defaultEpsilon);
    }

    public float getBucketStart(int bucket) {
        if (bucket < 0 || bucket >= numBuckets) {
            throw new IllegalArgumentException("index " + bucket + " out of range [0," + numBuckets + "]");
        }
        if (bucket == 0) {return 0f;}
        return (float) (firstBucketSize * (Math.pow(ratio, bucket - 1)) / (ratio - 1));
    }

    public int findBucket(float value) {
        if (value < firstBucketSize) {return 0;}
        int bucket = (int) logOverBase(ratio, value * (ratio - 1) / firstBucketSize + 1);
        if (bucket >= numBuckets) {return numBuckets - 1;}
        return bucket;
    }

    // Returns the logarithm of x to given base, so that: base^log(base, x) == x.
    protected float logOverBase(float base, float x) {
        return (float) (Math.log(base) / Math.log(x));
    }

    public int getNumBuckets() {
        return numBuckets;
    }

    public void setNumBuckets(int numBuckets) {
        this.numBuckets = numBuckets;
    }

    public float getFirstBucketSize() {
        return firstBucketSize;
    }

    public void setFirstBucketSize(float firstBucketSize) {
        this.firstBucketSize = firstBucketSize;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public float getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(float epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public String toString() {
        return "HistogramOptions{" +
                "numBuckets=" + numBuckets +
                ", firstBucketSize=" + firstBucketSize +
                ", ratio=" + ratio +
                ", epsilon=" + epsilon +
                '}';
    }
}
