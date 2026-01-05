package com.alipay.utils;

import com.alibaba.hitsdb.client.value.request.Point;
import com.alibaba.hitsdb.client.value.request.Query;
import com.alibaba.hitsdb.client.value.request.SubQuery;
import com.alibaba.hitsdb.client.value.response.QueryResult;
import com.alipay.antc.metric.client.MetricClient;
import com.alipay.model.metrics.OrignMetrics;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static com.alipay.consts.MetricsConstants.CERESDB_ENV;

/**
 * @author sansi.xy
 * @date 2024/7/2
 */
public class MetricsUtil {

  /**
   * 写入metrics点
   *
   * @param point
   * @return boolean
   */
  public static boolean writePoint(Point point) {
    return MetricClient.savePoint(CERESDB_ENV, point);
  }

  /**
   * 执行子查询
   *
   * @param subQuery 子查询
   * @param start startTime
   * @param end endTime
   * @return 查询结果
   */
  public static List<OrignMetrics> executeQuery(SubQuery subQuery, Long start, Long end) {
    Query query =
        Query.start(start)
            .end(end)
            .timezone(TimeZone.getTimeZone(ZoneId.systemDefault()))
            .sub(subQuery)
            .build();

    List<QueryResult> queryRs = MetricClient.query(CERESDB_ENV, query);
    if (queryRs == null || queryRs.isEmpty()) {
      return new ArrayList<>();
    }

    return queryRs.stream().map(OrignMetrics::fromQueryResult).collect(Collectors.toList());
  }
}
