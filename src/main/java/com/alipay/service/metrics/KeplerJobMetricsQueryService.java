package com.alipay.service.metrics;

import com.alibaba.hitsdb.client.value.request.SubQuery;
import com.alibaba.hitsdb.client.value.type.Aggregator;
import com.alipay.model.metrics.OrignMetrics;
import com.alipay.utils.MetricsUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sansi.xy
 * @date 1/5/26
 */

public class KeplerJobMetricsQueryService {

  public static final SubQuery.Builder CPU_QUERY_QUERY =
      new SubQuery.Builder("CpuUsedRatio", Aggregator.SUM).downsample("5m-avg");
  public static final SubQuery.Builder MEM_QUERY_QUERY =
      new SubQuery.Builder("MemoryUsed", Aggregator.SUM) // also refer "HeapMemory"
          .downsample("5m-avg");

  public List<OrignMetrics> query(com.alipay.service.metrics.MetricsEnum metricsEnum, String jobName, Long start, Long end) {
    Map<String, String> tags =
        new HashMap<>() {
          {
            put("metaType", "COMPONENT");
            put("topology", jobName);
          }
        };

    switch (metricsEnum) {
      case KEPLER_TASK_CPU -> {
        return MetricsUtil.executeQuery(CPU_QUERY_QUERY.tag(tags).build(), start, end);
      }
      case KEPLER_TASK_MEM -> {
        return MetricsUtil.executeQuery(MEM_QUERY_QUERY.tag(tags).build(), start, end);
      }
      default -> throw new IllegalArgumentException("Invalid query enum");
    }
  }
}
