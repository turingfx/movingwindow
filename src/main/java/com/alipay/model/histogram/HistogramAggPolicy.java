package com.alipay.model.histogram;

/**
 * @author sansi.xy
 * @date 12/29/25
 */
public class HistogramAggPolicy {
  HistogramAggType type;
  double percentile;

  public HistogramAggPolicy(HistogramAggType type, double percentile) {
    this.type = type;
    this.percentile = percentile;
  }

  public HistogramAggType getType() {
    return type;
  }

  public void setType(HistogramAggType type) {
    this.type = type;
  }

  public double getPercentile() {
    return percentile;
  }

  public void setPercentile(double percentile) {
    this.percentile = percentile;
  }
}
