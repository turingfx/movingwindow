package com.alipay.consts;

import java.time.Duration;

/**
 * @author sansi.xy
 * @date 2025/12/25
 */
public class HistogramConsts {
  // --- Histogram conf
  // minSampleWeight is the minimal weight of any sample (prior to including decaying factor)
  public static final float minSampleWeight = 0.1f;
  // epsilon is the minimal weight kept in histograms, it should be small enough that old samples
  // (just inside MemoryAggregationWindowLength) added with minSampleWeight are still kept
  public static final float defaultEpsilon = 0.001f * minSampleWeight;
  // DefaultHistogramBucketSizeGrowth is the default value for HistogramBucketSizeGrowth.
  public static final float defaultHistogramBucketSizeGrowth =
      0.05f; // Make each bucket 5% larger than the previous one.
  // When the decay factor exceeds 2^maxDecayExponent the histogram is
  // renormalized by shifting the decay start time forward.
  public static final int maxDecayExponent = 100;

  // DefaultMemoryAggregationIntervalCount is the default value for MemoryAggregationIntevalCount.
  public static final int defaultMemoryAggregationIntervalCount = 8;
  // DefaultMemoryAggregationInterval is the default value for MemoryAggregationInterval.
  // which the peak memory usage is computed.
  public static final Duration defaultMemoryAggregationInterval = Duration.ofHours(24);
  // DefaultMemoryHistogramDecayHalfLife is the default value for MemoryHistogramDecayHalfLife.
  public static final Duration defaultMemoryHistogramDecayHalfLife = Duration.ofHours(24);
  // DefaultCPUHistogramDecayHalfLife is the default value for CPUHistogramDecayHalfLife.
  // CPU usage sample to lose half of its weight.
  public static final Duration defaultCPUHistogramDecayHalfLife = Duration.ofHours(24);
}
