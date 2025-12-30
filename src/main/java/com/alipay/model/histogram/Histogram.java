package com.alipay.model.histogram;

import java.util.Arrays;

import static com.alipay.utils.DoubleUtil.round;

public class Histogram {
    protected HistogramOptions options;
    protected double[]          bucketWeight;
    protected double            totalWeight;
    protected int              minBucket;
    protected int              maxBucket;

    public Histogram(HistogramOptions options) {
        this.options = options;
        this.bucketWeight = new double[options.getNumBuckets()];
        this.totalWeight = 0f;
        this.minBucket = options.getNumBuckets() - 1;
        this.maxBucket = 0;
    }

    public void scala(double factor) {
        if (factor < 0) {
            throw new RuntimeException("scale factor must be non-negative");
        }
        for (int bucket = minBucket; bucket <= maxBucket; bucket++) {
            bucketWeight[bucket] = round(bucketWeight[bucket] * factor);
        }

        totalWeight = round(totalWeight * factor);
        updateMinAndMaxBucket();
    }

    public boolean clusterAtTheExtremes(double tolerantPercentage) {
        if (bucketWeight[0] / totalWeight >= tolerantPercentage) {return true;} else if (
                bucketWeight[bucketWeight.length - 1] / totalWeight >= tolerantPercentage) {return true;}
        return false;
    }

    public void addSample(double value, double weight, Long time) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight must be non-positive");
        }
        int bucket = options.findBucket(value);
        bucketWeight[bucket] += weight;
        bucketWeight[bucket] = round(bucketWeight[bucket]);
        totalWeight += weight;
        totalWeight = round(totalWeight);
        if (bucket < minBucket && bucketWeight[bucket] >= options.getEpsilon()) {
            minBucket = bucket;
        }
        if (bucket > maxBucket && bucketWeight[bucket] >= options.getEpsilon()) {
            maxBucket = bucket;
        }
    }

    public void subtractSample(double value, double weight, Long time) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight must be non-positive");
        }

        int bucket = options.findBucket(value);
        double epsilon = options.getEpsilon();

        totalWeight = safeSubtract(totalWeight, weight, epsilon);
        bucketWeight[bucket] = safeSubtract(bucketWeight[bucket], weight, epsilon);

        updateMinAndMaxBucket();
    }

    private double safeSubtract(double value, double sub, double epsilon) {
        value -= sub;
        if (value < epsilon) {
            return 0;
        }

        return value;
    }

    public void merge(Histogram o) {
        if (options != o.options) {
            throw new IllegalArgumentException("Cannot merge histogram with different options");
        }
        for (int bucket = minBucket; bucket <= maxBucket; bucket++) {
            bucketWeight[bucket] += o.bucketWeight[bucket];
            bucketWeight[bucket] = round(bucketWeight[bucket]);
        }
        totalWeight += round(o.totalWeight);
        totalWeight = round(totalWeight);
        if (o.minBucket < minBucket) {
            minBucket = o.minBucket;
        }
        if (o.maxBucket > maxBucket) {
            maxBucket = o.maxBucket;
        }
    }

    public double percentile(double percentile) {
        if (isEmpty()) {return 0;}
        double partialSum = 0;
        double threshold = round(percentile * totalWeight);
        int bucket = minBucket;
        for (; bucket < maxBucket; bucket++) {
            partialSum += bucketWeight[bucket];
            partialSum = round(partialSum);
            if (partialSum >= threshold) {
                break;
            }
        }

        if (bucket < options.getNumBuckets() - 1) {
            return options.getBucketStart(bucket + 1);
        }
        // Return the start of the last bucket (note that the last bucket
        // doesn't have an upper bound).
        return options.getBucketStart(bucket);
    }

    public double average() {
        if (isEmpty()) {return 0;}
        double sum = 0;
        int bucket = minBucket;
        for (; bucket <= maxBucket; bucket++) {
            int startBucket = bucket;
            if (bucket < options.getNumBuckets() - 1) {
                startBucket += 1;
            }
            double bucketLeftValue = options.getBucketStart(startBucket);
            sum += bucketWeight[bucket] * bucketLeftValue;
        }
        return round(sum) / round(totalWeight);
    }

    public double max() {
        if (isEmpty()) {return 0;}
        if (maxBucket < options.getNumBuckets() - 1) {
            return options.getBucketStart(maxBucket + 1);
        }
        return options.getBucketStart(maxBucket);
    }

    public double maxBucketWeight() {
        double maxBucketWeight = 0;
        for (int bucket = minBucket; bucket <= maxBucket; bucket++) {
            if (maxBucketWeight == 0) {
                maxBucketWeight += bucketWeight[bucket];
            } else if (maxBucketWeight < bucketWeight[bucket]) {
                maxBucketWeight = bucketWeight[bucket];
            }
        }
        return round(maxBucketWeight);
    }

    public boolean isEmpty() {
        return bucketWeight[minBucket] < options.getEpsilon();
    }

    // Adjusts the value of MinBucket and MaxBucket after any operation that
    // decreases weights.
    private void updateMinAndMaxBucket() {
        double epsilon = options.getEpsilon();
        int lastBucket = options.getNumBuckets() - 1;
        while (bucketWeight[minBucket] < epsilon && minBucket < lastBucket) {
            minBucket++;
        }
        while (bucketWeight[maxBucket] < epsilon && maxBucket > 0) {
            maxBucket--;
        }
    }

    public double agg(HistogramAggPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("no policy found in agg policy");
        }
        switch (policy.type) {
            case MAX -> {return max();}
            case AVERAGE -> {return average();}
            case Percentile -> {return percentile(policy.percentile);}
            default -> {throw new IllegalArgumentException("No agg policy with " + policy.type);}
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Histogram other)) {
            return false;
        }

        if (options != other.options || minBucket != other.minBucket || maxBucket != other.maxBucket) {return false;}
        for (int bucket = minBucket; bucket <= maxBucket; bucket++) {
            double diff = bucketWeight[bucket] - other.bucketWeight[bucket];
            if (diff > 1e-15 || diff < -1e-15) {return false;}
        }
        return true;
    }

    @Override
    public String toString() {
        return "Histogram{" + "\n" +
                "option=" + options.toString() + ",\n" +
                "bucketWeight=" + Arrays.toString(bucketWeight) + ",\n" +
                "totalWeight=" + totalWeight + ",\n" +
                "minBucket=" + minBucket + ",\n" +
                "minBucket=" + maxBucket +
                '}';
    }
}
