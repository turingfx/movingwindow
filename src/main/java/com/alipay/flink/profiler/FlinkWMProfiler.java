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

package com.alipay.flink.profiler;

import com.alipay.conf.WMProfilerConfig;
import com.alipay.flink.HistogramConst;
import com.alipay.flink.Resource;
import com.alipay.model.histogram.DecayingHistogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class FlinkWMProfiler implements IMovingWindowProfiler {
    private static final Logger LOG = LoggerFactory.getLogger(FlinkWMProfiler.class);

    private String               profileKey;
    private WMProfilerConfig     config;
    private FlinkWMProfilerState state;
    private Long lastModifiedTime;

    private boolean available;

    public FlinkWMProfiler(WMProfilerConfig config, String profileKey) {
        DecayingHistogram cpuHistogram =
                new DecayingHistogram(
                        HistogramConst.getCpuHistogramOptions(), config.getCpuHalfLiftMs());
        DecayingHistogram memInMBHistogram =
                new DecayingHistogram(
                        HistogramConst.getMemInMBHistogramOptions(), config.getMemHalfLiftMs());

        this.config = config;
        this.state = new FlinkWMProfilerState(cpuHistogram, memInMBHistogram, "default");
        this.lastModifiedTime = 0L;
        this.profileKey = profileKey;
        available = false;
    }

    /**
     * If we use ClusterManager realtime metrics, we have no method to get history metrics.<br>
     * So this method is deprecated right now.
     */
    @Deprecated
    public Optional<Resource> getRecommendProfileWithTimeRange(
            BiFunction<Long, Long, List<Resource>> queryMetrics, Long startTime, Long endTime) {
        List<Resource> metrics = queryMetrics.apply(startTime, endTime);
        feedData(metrics, System.currentTimeMillis());
        return fetchProfile();
    }

    /** feed a sample to profiler, note it is thread safe. */
    public void feedData(List<Resource> resources, Long timestamp) {
        if (!available) {
            available = true;
        }

        double curMemMax = 0;
        for (Resource resource : resources) {
            double cpuUsed = resource.getCpuCores();
            // NOTE k8s vpa use cpu request as weight in order to decrease history weight after
            // increasing cpu request stats cpu
            state.getCpuHistogram().addSample(cpuUsed, 1.0, timestamp);
            if (timestamp - config.getMemAggregationWindowMs() >= lastModifiedTime
                    && System.currentTimeMillis() - timestamp <= config.getExpirationMs()) {
                if (curMemMax > 0) {
                    state.getMemHistogram().addSample(curMemMax, 1.0, timestamp);
                }
                curMemMax = 0;
                lastModifiedTime = timestamp;
            }

            curMemMax = Math.max(curMemMax, resource.getMemInMB());
        }

        if (curMemMax > 0) {
            state.getMemHistogram().addSample(curMemMax, 1.0, timestamp);
        }
    }

    public void feedData(Resource resource, Long timestamp) {
        if (!available) {
            available = true;
        }

        try {
            double cpuUsed = resource.getCpuCores();
            state.getCpuHistogram().addSample(cpuUsed, 1.0, timestamp);
            if (timestamp - config.getMemAggregationWindowMs() >= lastModifiedTime) {
                state.getMemHistogram().addSample(resource.getMemInMB(), 1.0, timestamp);
                lastModifiedTime = timestamp;
            }
        } catch (Exception e) {
            LOG.error("Feed data error", e);
            // does not block metric submissions
        }
    }

    /**
     * feed a sample to profiler, note it is thread safe. <br>
     * If profile is not available, return empty.
     */
    public Optional<Resource> fetchProfile() {
        if (!available) {
            return Optional.empty();
        }

        try {
            double rcCpuUsed =
                    state.getCpuHistogram()
                            .recommendByHistogram(config.getCpuAggPolicy(), config.getCpuAdjust());
            double rcMemInMBUsed =
                    state.getMemHistogram()
                            .recommendByHistogram(config.getMemAggPolicy(), config.getMemAdjust());

            return Optional.of(new Resource(rcCpuUsed, (int) rcMemInMBUsed));
        } catch (Exception e) {
            LOG.error("Get resource profile failed", e);
            return Optional.empty();
        }
    }

    public void mergeProfiler(FlinkWMProfiler other) {
        if (!config.equals(other.config)) {
            throw new IllegalArgumentException("Two profiler with Different config cannot merge!");
        }
        DecayingHistogram cpuHistogram = this.getState().getCpuHistogram();
        DecayingHistogram memHistogram = this.getState().getMemHistogram();

        cpuHistogram.merge(other.getState().getCpuHistogram());
        memHistogram.merge(other.getState().getMemHistogram());

        this.available = available && other.isAvailable();
    }

    public WMProfilerConfig getConfig() {
        return config;
    }

    public void setConfig(WMProfilerConfig config) {
        this.config = config;
    }

    public FlinkWMProfilerState getState() {
        return state;
    }

    public void setState(FlinkWMProfilerState state) {
        this.state = state;
    }

    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
