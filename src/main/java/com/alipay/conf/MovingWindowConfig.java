package com.alipay.conf;

import com.alipay.model.profiler.MarginAdjust;

/**
 * @author sansi.xy
 * @date 12/31/25
 */
public class MovingWindowConfig {
  private int          CpuHalfLiftMill;
  private int          MemHalfLiftMill;
  private MarginAdjust cpuAdjust;
  private MarginAdjust memAdjust;
  private int          minHistoryMetricsLifeTime;
  private int          expirationSeconds;
}
