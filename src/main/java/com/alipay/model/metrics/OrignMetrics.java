package com.alipay.model.metrics;

import com.alibaba.hitsdb.client.value.response.QueryResult;

import java.util.LinkedHashMap;
import java.util.Map;

public class OrignMetrics {

  private String                      metric;
  private Map<String, String>         tags;
  private LinkedHashMap<Long, Object> dps = new LinkedHashMap<>();

  public static OrignMetrics fromQueryResult(QueryResult queryResult) {
    OrignMetrics orignMetrics = new OrignMetrics();
    orignMetrics.setMetric(queryResult.getMetric());
    orignMetrics.setTags(queryResult.getTags());
    orignMetrics.setDps(queryResult.getDps());
    return orignMetrics;
  }

  public String getMetric() {
    return metric;
  }

  public void setMetric(String metric) {
    this.metric = metric;
  }

  public Map<String, String> getTags() {
    return tags;
  }

  public void setTags(Map<String, String> tags) {
    this.tags = tags;
  }

  public LinkedHashMap<Long, Object> getDps() {
    return dps;
  }

  public void setDps(LinkedHashMap<Long, Object> dps) {
    this.dps = dps;
  }
}
