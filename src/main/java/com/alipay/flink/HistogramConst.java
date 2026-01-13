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

package com.alipay.flink;

import com.alipay.model.histogram.HistogramOptions;

/** {@link HistogramOptions} default const. */
public class HistogramConst {
    // todo use "ClusterMode Conf" to set
    // minSampleWeight is the minimal weight of any sample (prior to including decaying factor)
    public static final float MIN_SAMPLE_WEIGHT = 0.1f;
    // epsilon is the minimal weight kept in histograms, it should be small enough that old samples
    // (just inside MemoryAggregationWindowLength) added with minSampleWeight are still kept
    public static final float DEFAULT_EPSILON = 0.001f * MIN_SAMPLE_WEIGHT;
    // DefaultHistogramBucketSizeGrowth is the default value for HistogramBucketSizeGrowth.
    public static final float DEFAULT_HISTOGRAM_BUCKET_SIZE_GROWTH =
            0.05f; // Make each bucket 5% larger than the previous one.
    // When the decay factor exceeds 2^maxDecayExponent the histogram is
    // renormalized by shifting the decay start time forward.
    public static final int MAX_DECAY_EXPONENT = 100;

    public static HistogramOptions getCpuHistogramOptions() {
        // ceil is cu,cpu range is 0.01 ~ 1000
        return new HistogramOptions(
                1000, 0.01, 1 + DEFAULT_HISTOGRAM_BUCKET_SIZE_GROWTH, DEFAULT_EPSILON);
    }

    public static HistogramOptions getMemHistogramOptions() {
        // ceil is B,mem range is 10MB ~ 1TB
        return new HistogramOptions(
                1e12, 1e7, 1 + DEFAULT_HISTOGRAM_BUCKET_SIZE_GROWTH, DEFAULT_EPSILON);
    }

    public static HistogramOptions getMemInMBHistogramOptions() {
        // ceil is MB,value range is also 10MB ~ 1TB
        return new HistogramOptions(
                1e6, 10, 1 + DEFAULT_HISTOGRAM_BUCKET_SIZE_GROWTH, DEFAULT_EPSILON);
    }

    // ALSO CONTAINS SOME PROFILER CONST
    public static final long DEFAULT_CPU_HALF_LIFE_MS = /*12h*/ 12 * 60 * 60 * 1000;
    public static final long DEFAULT_MEM_HALF_LIFE_MS = /*48h*/ 48 * 60 * 60 * 1000;
    public static final double DEFAULT_PROFILE_CPU_PERCENTILE = 0.9;
    public static final double DEFAULT_PROFILE_MEM_PERCENTILE = 0.9;
    public static final double DEFAULT_PROFILE_CPU_ADJUST_RATIO = 0.1;
    public static final double DEFAULT_PROFILE_MEM_ADJUST_RATIO = 0.2;

    public static final long DEFAULT_EXPIRATION_MS = /* 7d */ 7 * 86400_000;
    public static final long DEFAULT_METRICS_AGG_WIN_MS = /* 5min */ 300_000;
    public static final long DEFAULT_MIN_MEM_LIFE_TIME_MS = 216000_000;
    public static final double DEFAULT_ADJUST_MIN_STEP = 2.097152E8;
    public static final double DEFAULT_ADJUST_MAX_STEP = 1.073741824E9;
}
