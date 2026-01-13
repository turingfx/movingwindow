package com.alipay.conf;

import com.alipay.flink.HistogramConst;
import com.alipay.model.histogram.HistogramAggPolicy;
import com.alipay.model.histogram.HistogramAggType;
import com.alipay.model.profiler.MarginAdjust;

/**
 * @author sansi.xy
 * @date 12/31/25
 */
public class WMProfilerConfig {
  private static final long serialVersionUID = -3121663762000154497L;

  private long cpuHalfLiftMs;
  private long memHalfLiftMs;

  private long expirationMs;
  private long aggregationWindowMs;

  private long minHistoryMetricsLifeTimeMs;
  private HistogramAggPolicy cpuAggPolicy;
  private HistogramAggPolicy memAggPolicy;
  private MarginAdjust cpuAdjust;
  private MarginAdjust memAdjust;

  public WMProfilerConfig() {}

  public WMProfilerConfig(
          long cpuHalfLiftMs,
          long memHalfLiftMs,
          long expirationMs,
          long aggregationWindowMs,
          long minHistoryMetricsLifeTimeMs,
          HistogramAggPolicy cpuAggPolicy,
          HistogramAggPolicy memAggPolicy,
          MarginAdjust cpuAdjust,
          MarginAdjust memAdjust) {
    this.cpuHalfLiftMs = cpuHalfLiftMs;
    this.memHalfLiftMs = memHalfLiftMs;
    this.expirationMs = expirationMs;
    this.aggregationWindowMs = aggregationWindowMs;
    this.minHistoryMetricsLifeTimeMs = minHistoryMetricsLifeTimeMs;
    this.cpuAggPolicy = cpuAggPolicy;
    this.memAggPolicy = memAggPolicy;
    this.cpuAdjust = cpuAdjust;
    this.memAdjust = memAdjust;
  }

  // todo default conf value will use "clusterModeConf" to set
  public static WMProfilerConfig getDefault() {
    WMProfilerConfig defaultConfig = new WMProfilerConfig();
    defaultConfig.cpuHalfLiftMs = HistogramConst.DEFAULT_CPU_HALF_LIFE_MS;
    defaultConfig.memHalfLiftMs = HistogramConst.DEFAULT_MEM_HALF_LIFE_MS;

    defaultConfig.expirationMs = HistogramConst.DEFAULT_EXPIRATION_MS;
    defaultConfig.aggregationWindowMs = HistogramConst.DEFAULT_METRICS_AGG_WIN_MS;

    defaultConfig.minHistoryMetricsLifeTimeMs = HistogramConst.DEFAULT_MIN_MEM_LIFE_TIME_MS;
    defaultConfig.cpuAggPolicy =
            new HistogramAggPolicy(
                    HistogramAggType.Percentile, HistogramConst.DEFAULT_PROFILE_CPU_PERCENTILE);
    defaultConfig.memAggPolicy =
            new HistogramAggPolicy(
                    HistogramAggType.Percentile,
                    HistogramConst
                            .DEFAULT_PROFILE_MEM_PERCENTILE); // consider oom to inc this value
    defaultConfig.cpuAdjust =
            new MarginAdjust(
                    HistogramConst.DEFAULT_PROFILE_CPU_ADJUST_RATIO,
                    HistogramConst.DEFAULT_ADJUST_MIN_STEP,
                    HistogramConst.DEFAULT_ADJUST_MAX_STEP);
    defaultConfig.memAdjust =
            new MarginAdjust(
                    HistogramConst.DEFAULT_PROFILE_MEM_ADJUST_RATIO,
                    HistogramConst.DEFAULT_ADJUST_MIN_STEP,
                    HistogramConst.DEFAULT_ADJUST_MAX_STEP);
    return defaultConfig;
  }

  public long getCpuHalfLiftMs() {
    return cpuHalfLiftMs;
  }

  public void setCpuHalfLiftMs(long cpuHalfLiftMs) {
    this.cpuHalfLiftMs = cpuHalfLiftMs;
  }

  public long getMemHalfLiftMs() {
    return memHalfLiftMs;
  }

  public void setMemHalfLiftMs(long memHalfLiftMs) {
    this.memHalfLiftMs = memHalfLiftMs;
  }

  public long getExpirationMs() {
    return expirationMs;
  }

  public void setExpirationMs(long expirationMs) {
    this.expirationMs = expirationMs;
  }

  public long getMemAggregationWindowMs() {
    return aggregationWindowMs;
  }

  public void setMemAggregationWindowMs(long memAggregationWindowMs) {
    this.aggregationWindowMs = memAggregationWindowMs;
  }

  public long getMinHistoryMetricsLifeTimeMs() {
    return minHistoryMetricsLifeTimeMs;
  }

  public void setMinHistoryMetricsLifeTimeMs(long minHistoryMetricsLifeTimeMs) {
    this.minHistoryMetricsLifeTimeMs = minHistoryMetricsLifeTimeMs;
  }

  public HistogramAggPolicy getCpuAggPolicy() {
    return cpuAggPolicy;
  }

  public void setCpuAggPolicy(HistogramAggPolicy cpuAggPolicy) {
    this.cpuAggPolicy = cpuAggPolicy;
  }

  public HistogramAggPolicy getMemAggPolicy() {
    return memAggPolicy;
  }

  public void setMemAggPolicy(HistogramAggPolicy memAggPolicy) {
    this.memAggPolicy = memAggPolicy;
  }

  public MarginAdjust getCpuAdjust() {
    return cpuAdjust;
  }

  public void setCpuAdjust(MarginAdjust cpuAdjust) {
    this.cpuAdjust = cpuAdjust;
  }

  public MarginAdjust getMemAdjust() {
    return memAdjust;
  }

  public void setMemAdjust(MarginAdjust memAdjust) {
    this.memAdjust = memAdjust;
  }
}
