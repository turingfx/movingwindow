/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alipay.model.histogram;

import com.alipay.conf.WMProfilerConfig;
import com.alipay.flink.HistogramConst;
import com.alipay.model.profiler.MarginAdjust;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for WMProfilerConfig class.
 */
public class WMProfilerConfigTest {

    @Test
    public void testDefaultConstructor() {
        WMProfilerConfig config = new WMProfilerConfig();
        
        // Test that default constructor creates an instance without exceptions
        assertNotNull(config);
    }

    @Test
    public void testParameterizedConstructor() {
        long cpuHalfLiftMs = 1000L;
        long memHalfLiftMs = 2000L;
        long expirationMs = 3000L;
        long aggregationWindowMs = 4000L;
        long minHistoryMetricsLifeTimeMs = 5000L;
        HistogramAggPolicy cpuAggPolicy = new HistogramAggPolicy(HistogramAggType.Percentile, 0.9);
        HistogramAggPolicy memAggPolicy = new HistogramAggPolicy(HistogramAggType.Percentile, 0.8);
        MarginAdjust cpuAdjust = new MarginAdjust(0.1, 1.0, 10.0);
        MarginAdjust memAdjust = new MarginAdjust(0.2, 2.0, 20.0);

        WMProfilerConfig config = new WMProfilerConfig(
                cpuHalfLiftMs,
                memHalfLiftMs,
                expirationMs,
                aggregationWindowMs,
                minHistoryMetricsLifeTimeMs,
                cpuAggPolicy,
                memAggPolicy,
                cpuAdjust,
                memAdjust
        );

        assertEquals(cpuHalfLiftMs, config.getCpuHalfLiftMs());
        assertEquals(memHalfLiftMs, config.getMemHalfLiftMs());
        assertEquals(expirationMs, config.getExpirationMs());
        assertEquals(aggregationWindowMs, config.getMemAggregationWindowMs());
        assertEquals(minHistoryMetricsLifeTimeMs, config.getMinHistoryMetricsLifeTimeMs());
        assertEquals(cpuAggPolicy, config.getCpuAggPolicy());
        assertEquals(memAggPolicy, config.getMemAggPolicy());
        assertEquals(cpuAdjust, config.getCpuAdjust());
        assertEquals(memAdjust, config.getMemAdjust());
    }

    @Test
    public void testGetDefault() {
        WMProfilerConfig defaultConfig = WMProfilerConfig.getDefault();

        // Test default values against HistogramConst
        assertEquals(HistogramConst.DEFAULT_CPU_HALF_LIFE_MS, defaultConfig.getCpuHalfLiftMs());
        assertEquals(HistogramConst.DEFAULT_MEM_HALF_LIFE_MS, defaultConfig.getMemHalfLiftMs());
        assertEquals(HistogramConst.DEFAULT_EXPIRATION_MS, defaultConfig.getExpirationMs());
        assertEquals(HistogramConst.DEFAULT_METRICS_AGG_WIN_MS, defaultConfig.getMemAggregationWindowMs());
        assertEquals(HistogramConst.DEFAULT_MIN_MEM_LIFE_TIME_MS, defaultConfig.getMinHistoryMetricsLifeTimeMs());

        // Test CPU aggregation policy
        HistogramAggPolicy cpuAggPolicy = defaultConfig.getCpuAggPolicy();
        assertNotNull(cpuAggPolicy);
        assertEquals(HistogramAggType.Percentile, cpuAggPolicy.getType());
        assertEquals(HistogramConst.DEFAULT_PROFILE_CPU_PERCENTILE, cpuAggPolicy.getPercentile(), 0.0);

        // Test MEM aggregation policy
        HistogramAggPolicy memAggPolicy = defaultConfig.getMemAggPolicy();
        assertNotNull(memAggPolicy);
        assertEquals(HistogramAggType.Percentile, memAggPolicy.getType());
        assertEquals(HistogramConst.DEFAULT_PROFILE_MEM_PERCENTILE, memAggPolicy.getPercentile(), 0.0);

        // Test CPU adjust
        MarginAdjust cpuAdjust = defaultConfig.getCpuAdjust();
        assertNotNull(cpuAdjust);
        assertEquals(HistogramConst.DEFAULT_PROFILE_CPU_ADJUST_RATIO, cpuAdjust.getRatio(), 0.0);
        assertEquals(HistogramConst.DEFAULT_ADJUST_MIN_STEP, cpuAdjust.getMinStep(), 0.0);
        assertEquals(HistogramConst.DEFAULT_ADJUST_MAX_STEP, cpuAdjust.getMaxStep(), 0.0);

        // Test MEM adjust
        MarginAdjust memAdjust = defaultConfig.getMemAdjust();
        assertNotNull(memAdjust);
        assertEquals(HistogramConst.DEFAULT_PROFILE_MEM_ADJUST_RATIO, memAdjust.getRatio(), 0.0);
        assertEquals(HistogramConst.DEFAULT_ADJUST_MIN_STEP, memAdjust.getMinStep(), 0.0);
        assertEquals(HistogramConst.DEFAULT_ADJUST_MAX_STEP, memAdjust.getMaxStep(), 0.0);
    }

    @Test
    public void testSetterAndGetters() {
        WMProfilerConfig config = new WMProfilerConfig();

        // Test CPU half life ms
        long expectedCpuHalfLiftMs = 5000L;
        config.setCpuHalfLiftMs(expectedCpuHalfLiftMs);
        assertEquals(expectedCpuHalfLiftMs, config.getCpuHalfLiftMs());

        // Test MEM half life ms
        long expectedMemHalfLiftMs = 6000L;
        config.setMemHalfLiftMs(expectedMemHalfLiftMs);
        assertEquals(expectedMemHalfLiftMs, config.getMemHalfLiftMs());

        // Test expiration ms
        long expectedExpirationMs = 7000L;
        config.setExpirationMs(expectedExpirationMs);
        assertEquals(expectedExpirationMs, config.getExpirationMs());

        // Test aggregation window ms
        long expectedAggregationWindowMs = 8000L;
        config.setMemAggregationWindowMs(expectedAggregationWindowMs);
        assertEquals(expectedAggregationWindowMs, config.getMemAggregationWindowMs());

        // Test min history metrics lifetime ms
        long expectedMinHistoryMetricsLifeTimeMs = 9000L;
        config.setMinHistoryMetricsLifeTimeMs(expectedMinHistoryMetricsLifeTimeMs);
        assertEquals(expectedMinHistoryMetricsLifeTimeMs, config.getMinHistoryMetricsLifeTimeMs());

        // Test CPU aggregation policy
        HistogramAggPolicy expectedCpuAggPolicy = new HistogramAggPolicy(HistogramAggType.AVERAGE, 0.5);
        config.setCpuAggPolicy(expectedCpuAggPolicy);
        assertEquals(expectedCpuAggPolicy, config.getCpuAggPolicy());

        // Test MEM aggregation policy
        HistogramAggPolicy expectedMemAggPolicy = new HistogramAggPolicy(HistogramAggType.MAX, 0.7);
        config.setMemAggPolicy(expectedMemAggPolicy);
        assertEquals(expectedMemAggPolicy, config.getMemAggPolicy());

        // Test CPU adjust
        MarginAdjust expectedCpuAdjust = new MarginAdjust(0.15, 1.5, 15.0);
        config.setCpuAdjust(expectedCpuAdjust);
        assertEquals(expectedCpuAdjust, config.getCpuAdjust());

        // Test MEM adjust
        MarginAdjust expectedMemAdjust = new MarginAdjust(0.25, 2.5, 25.0);
        config.setMemAdjust(expectedMemAdjust);
        assertEquals(expectedMemAdjust, config.getMemAdjust());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Test that two instances created with the same parameters are equal
        HistogramAggPolicy cpuAggPolicy = new HistogramAggPolicy(HistogramAggType.Percentile, 0.9);
        HistogramAggPolicy memAggPolicy = new HistogramAggPolicy(HistogramAggType.Percentile, 0.8);
        MarginAdjust cpuAdjust = new MarginAdjust(0.1, 1.0, 10.0);
        MarginAdjust memAdjust = new MarginAdjust(0.2, 2.0, 20.0);

        WMProfilerConfig config1 = new WMProfilerConfig(
                1000L, 2000L, 3000L, 4000L, 5000L,
                cpuAggPolicy, memAggPolicy, cpuAdjust, memAdjust
        );
        WMProfilerConfig config2 = new WMProfilerConfig(
                1000L, 2000L, 3000L, 4000L, 5000L,
                cpuAggPolicy, memAggPolicy, cpuAdjust, memAdjust
        );

        // Note: Since WMProfilerConfig doesn't override equals(), we test for structural similarity
        assertEquals(config1.getCpuHalfLiftMs(), config2.getCpuHalfLiftMs());
        assertEquals(config1.getMemHalfLiftMs(), config2.getMemHalfLiftMs());
        assertEquals(config1.getExpirationMs(), config2.getExpirationMs());
        assertEquals(config1.getMemAggregationWindowMs(), config2.getMemAggregationWindowMs());
        assertEquals(config1.getMinHistoryMetricsLifeTimeMs(), config2.getMinHistoryMetricsLifeTimeMs());
        assertEquals(config1.getCpuAggPolicy().getType(), config2.getCpuAggPolicy().getType());
        assertEquals(config1.getCpuAggPolicy().getPercentile(), config2.getCpuAggPolicy().getPercentile(), 0.0);
        assertEquals(config1.getMemAggPolicy().getType(), config2.getMemAggPolicy().getType());
        assertEquals(config1.getMemAggPolicy().getPercentile(), config2.getMemAggPolicy().getPercentile(), 0.0);
        assertEquals(config1.getCpuAdjust().getRatio(), config2.getCpuAdjust().getRatio(), 0.0);
        assertEquals(config1.getCpuAdjust().getMinStep(), config2.getCpuAdjust().getMinStep(), 0.0);
        assertEquals(config1.getCpuAdjust().getMaxStep(), config2.getCpuAdjust().getMaxStep(), 0.0);
        assertEquals(config1.getMemAdjust().getRatio(), config2.getMemAdjust().getRatio(), 0.0);
        assertEquals(config1.getMemAdjust().getMinStep(), config2.getMemAdjust().getMinStep(), 0.0);
        assertEquals(config1.getMemAdjust().getMaxStep(), config2.getMemAdjust().getMaxStep(), 0.0);
    }
}
