package com.alipay.model.metrics;

import com.alipay.utils.TimestampUtil;

/**
 * @author sansi.xy
 * @date 12/31/25
 */
public class BaseMetric {
  private String metricName;
  private Long timestamp;
  private Double value;

  public BaseMetric(String metricName, Long timestamp, Double value) {
    this.metricName = metricName;
    this.timestamp = timestamp;
    this.value = value;
  }

  public String toString() {
    return "BaseMetric{"
        + "metricName='"
        + metricName
        + '\''
        + ", time="
        + TimestampUtil.timestampMsToString(timestamp)
        + ", value="
        + value
        + '}';
  }

  public String getMetricName() {
    return metricName;
  }

  public void setMetricName(String metricName) {
    this.metricName = metricName;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }
}
