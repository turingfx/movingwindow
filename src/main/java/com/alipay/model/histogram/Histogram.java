package com.alipay.model.histogram;

public class Histogram {
    protected HistogramOptions options;
    protected float[]          bucketWeight;
    protected float            totalWeight;
    protected int              minBucket;
    protected int              maxBucket;

    public Histogram(HistogramOptions options) {
        this.options = options;
        this.bucketWeight = new float[options.getNumBuckets()];
        this.totalWeight = 0f;
        this.minBucket = options.getNumBuckets() -1;
        this.maxBucket = 0;
    }

    public void scala(float factor) {
        if (factor < 0) {
            throw new RuntimeException("scale factor must be non-negative");
        }
        for (int bucket = minBucket; bucket <= maxBucket; bucket++) {
            bucketWeight[bucket] *= factor;
        }

        totalWeight *= factor;
        updateMinAndMaxBucket();
    }

    public boolean clusterAtTheExtremes(float tolerantPercentage) {
        if (bucketWeight[0] / totalWeight >= tolerantPercentage) {return true;} else if (
                bucketWeight[bucketWeight.length - 1] / tolerantPercentage >= tolerantPercentage) {return true;}
        return false;
    }

    public void addSample(float value, float weight, Long time) {
        if (weight < 0f) {
            throw new IllegalArgumentException("Weight must be non-positive");
        }
        int bucket = options.findBucket(value);
        totalWeight += weight;
        if (bucket < minBucket && bucketWeight[bucket] >= options.getEpsilon()) {
            minBucket = bucket;
        }
        if (bucket > maxBucket && bucketWeight[bucket] >= options.getEpsilon()) {
            maxBucket = bucket;
        }
    }

    public void subtractSample(float value, float weight, Long time) {
        if (weight < 0f) {
            throw new IllegalArgumentException("Weight must be non-positive");
        }

        int bucket = options.findBucket(value);
        float epsilon = options.getEpsilon();

        totalWeight = safeSubtract(totalWeight, weight, epsilon);
        bucketWeight[bucket] = safeSubtract(bucketWeight[bucket], weight, epsilon);

        updateMinAndMaxBucket();
    }

    private float safeSubtract(float value, float sub, float epsilon) {
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
        }
        totalWeight += o.totalWeight;
        if (o.minBucket < minBucket) {
            minBucket = o.minBucket;
        }
        if (o.maxBucket > maxBucket) {
            maxBucket = o.maxBucket;
        }
    }

    public float percentile(float percentile) {
        if (isEmpty()) {return 0f;}
        float partialSum = 0f;
        float threshold = percentile * totalWeight;
        int bucket = minBucket;
        for (; bucket <= maxBucket; bucket++) {
            partialSum += bucketWeight[bucket];
            if (partialSum >= threshold) {
                break;
            }
        }

        if (bucket < options.getNumBuckets() - 1) {return options.getBucketStart(bucket + 1);}
        // Return the start of the last bucket (note that the last bucket
        // doesn't have an upper bound).
        return options.getBucketStart(bucket);
    }

    public float average() {
        if (isEmpty()) {return 0f;}
        float sum = 0;
        int bucket = minBucket;
        for (; bucket <= maxBucket; bucket++) {
            int startBucket = bucket;
            if (bucket < options.getNumBuckets() - 1) {
                startBucket += 1;
            }
            sum += bucketWeight[bucket] + options.getBucketStart(startBucket);
        }
        return sum / totalWeight;
    }

    public float max() {
        if (isEmpty()) {return 0f;}
        if (maxBucket < options.getNumBuckets() - 1) {
            return options.getBucketStart(maxBucket + 1);
        }
        return options.getBucketStart(maxBucket);
    }

    public boolean isEmpty() {
        return bucketWeight[minBucket] < options.getEpsilon();
    }

    // Adjusts the value of MinBucket and MaxBucket after any operation that
    // decreases weights.
    private void updateMinAndMaxBucket() {
        float epsilon = options.getEpsilon();
        int lastBucket = options.getNumBuckets() - 1;
        while (bucketWeight[minBucket] < epsilon && minBucket < lastBucket) {
            minBucket++;
        }
        while (bucketWeight[maxBucket] < epsilon && maxBucket > 0) {
            maxBucket--;
        }
    }

    public float agg(HistogramAggPolicy policy){
        if (policy == null){
            throw new IllegalArgumentException("no policy found in agg policy");
        }
        switch (policy.type){
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

        if (options != other.options || minBucket != other.minBucket || maxBucket != other.maxBucket) return false;
        for (int bucket = minBucket; bucket <= maxBucket; bucket++){
            float diff = bucketWeight[bucket] - other.bucketWeight[bucket];
            if (diff > 1e-15 || diff < -1e-15) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Histogram{" +
                "option=" + options.toString() +
                "bucketWeight=" + bucketWeight +
                "totalWeight=" + totalWeight +
                "minBucket=" + minBucket +
                ", minBucket=" + maxBucket +
                '}';
    }
}
