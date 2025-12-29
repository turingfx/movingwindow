package com.alipay.model.histogram;

import java.util.Objects;

import static com.alipay.consts.HistogramConsts.maxDecayExponent;

/**
 * 半衰期窗口
 *
 * @author sansi.xy
 * @date 2025/12/25
 */

public class DecayingHistogram extends Histogram {
    private Long halfLife;
    private Long referenceTime;

    public DecayingHistogram(HistogramOptions options,Long halfLife, Long referenceTime) {
        super(options);
        this.halfLife = halfLife;
        this.referenceTime = referenceTime;
    }

    @Override
    public void addSample(float value, float weight, Long time) {
        super.addSample(value,weight * decayFactor(time), time);
    }

    @Override
    public void subtractSample(float value, float weight, Long time) {
        super.subtractSample(value, weight * decayFactor(time), time);
    }

    public void merge(DecayingHistogram o) {
        if (!Objects.equals(halfLife, o.halfLife)){
            throw new IllegalArgumentException("Cannot merge histogram with different halfLife");
        }
        if (referenceTime < o.referenceTime){
            shiftReferenceTimestamp(o.referenceTime);
        }else {
            o.shiftReferenceTimestamp(referenceTime);
        }
        super.merge(o);
    }


    public float decayFactor(Long timestamp) {
        // Max timestamp before the exponent grows too large.
        long maxAllowedTimestamp = referenceTime + halfLife * maxDecayExponent;
        if (timestamp > maxAllowedTimestamp) {
            shiftReferenceTimestamp(timestamp);
        }
        return (float) Math.pow(2, (double) (timestamp - referenceTime) / halfLife);
    }

    private void shiftReferenceTimestamp(Long newReferenceTimestamp) {
        // Make sure the decay start is an integer multiple of HalfLife.
        newReferenceTimestamp = Math.round((float) newReferenceTimestamp / halfLife) * halfLife;
        int exponent = Math.round((float) (referenceTime - newReferenceTimestamp) / halfLife);
        scala(Math.scalb(1f, exponent)); // Scale all weights by 2^exponent.
        referenceTime = newReferenceTimestamp;
    }

    public boolean isVaryDramatic(float percentile) {
        float maxBucketWeight = maxBucketWeight();
        return maxBucketWeight / totalWeight < percentile;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DecayingHistogram other)) {
            return false;
        }
        return Objects.equals(halfLife, other.halfLife) && Objects.equals(referenceTime, other.referenceTime) && super.equals(obj);
    }

    @Override
    public String toString() {
        return "DecayingHistogram{" +"\n"+
                "super=" + super.toString() +",\n"+
                "halfLife=" + halfLife +",\n"+
                "referenceTime=" + referenceTime +
                '}';
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    public Long getHalfLife() {
        return halfLife;
    }

    public void setHalfLife(Long halfLife) {
        this.halfLife = halfLife;
    }

    public Long getReferenceTime() {
        return referenceTime;
    }

    public void setReferenceTime(Long referenceTime) {
        this.referenceTime = referenceTime;
    }
}
