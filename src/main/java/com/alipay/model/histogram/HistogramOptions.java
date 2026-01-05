package com.alipay.model.histogram;

import static com.alipay.model.consts.HistogramConsts.defaultEpsilon;
import static com.alipay.model.consts.HistogramConsts.defaultHistogramBucketSizeGrowth;
import static com.alipay.utils.DoubleUtil.round;

import com.alipay.utils.DoubleUtil;

/**
 * @author sansi.xy
 * @date 2025/12/25
 */
public class HistogramOptions {
  private final int numBuckets;
  private final double firstBucketSize;
  private final double ratio;
  private final double epsilon;

  public HistogramOptions(double maxValue, double firstBucketSize, double ratio, double epsilon) {
    if (maxValue <= 0 || firstBucketSize <= 0 || ratio <= 1 || epsilon <= 0) {
      throw new IllegalArgumentException("invalid histogram options");
    }

    double a = logOverBase(ratio, maxValue * (ratio - 1) / firstBucketSize + 1);
    this.numBuckets = (int) (Math.ceil(a) + 1);
    this.firstBucketSize = firstBucketSize;
    this.ratio = ratio;
    this.epsilon = epsilon;
  }

  public static HistogramOptions getCpuHistogramOptions() {
    // ceil is cu,cpu range is 0.01 ~ 1000
    return new HistogramOptions(1000, 0.01, 1 + defaultHistogramBucketSizeGrowth, defaultEpsilon);
  }

  public static HistogramOptions getMemHistogramOptions() {
    // ceil is B,mem range is 10MB ~ 1TB
    return new HistogramOptions(1e12, 1e7, 1 + defaultHistogramBucketSizeGrowth, defaultEpsilon);
  }

  public double getBucketStart(int bucket) {
    if (bucket < 0 || bucket >= numBuckets) {
      throw new IllegalArgumentException(
          "index " + bucket + " out of range [0," + numBuckets + "]");
    }
    if (bucket == 0) {
      return 0;
    }
    return round(firstBucketSize * (Math.pow(ratio, bucket) - 1) / (ratio - 1));
  }

  public int findBucket(double value) {
    if (value < firstBucketSize) {
      return 0;
    }
    int bucket = (int) logOverBase(ratio, value * (ratio - 1) / firstBucketSize + 1);
    if (bucket >= numBuckets) {
      return numBuckets - 1;
    }
    return bucket;
  }

  // Returns the logarithm of x to given base,formula is log_y(x) = ln(x)/ln(y)
  protected double logOverBase(double base, double x) {
    return DoubleUtil.divide(Math.log(x), Math.log(base));
  }

  public int getNumBuckets() {
    return numBuckets;
  }

  public double getFirstBucketSize() {
    return firstBucketSize;
  }

  public double getRatio() {
    return ratio;
  }

  public double getEpsilon() {
    return epsilon;
  }

  @Override
  public String toString() {
    return "HistogramOptions{"
        + "\n"
        + "numBuckets="
        + numBuckets
        + ",\n"
        + "firstBucketSize="
        + firstBucketSize
        + ",\n"
        + "ratio="
        + ratio
        + ",\n"
        + "epsilon="
        + epsilon
        + '}';
  }
}
