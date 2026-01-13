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


import com.alipay.model.histogram.DecayingHistogram;

import java.io.Serializable;

/** State info in {@link FlinkWMProfiler}. */
public class FlinkWMProfilerState implements Serializable {
    private static final long serialVersionUID = 4085884902241078326L;

    private DecayingHistogram cpuHistogram;
    private DecayingHistogram memHistogram;
    private String jobVersion;

    public FlinkWMProfilerState(
            DecayingHistogram cpuHistogram, DecayingHistogram memHistogram, String jobVersion) {
        this.cpuHistogram = cpuHistogram;
        this.memHistogram = memHistogram;
        this.jobVersion = jobVersion;
    }

    public DecayingHistogram getCpuHistogram() {
        return cpuHistogram;
    }

    public void setCpuHistogram(DecayingHistogram cpuHistogram) {
        this.cpuHistogram = cpuHistogram;
    }

    public DecayingHistogram getMemHistogram() {
        return memHistogram;
    }

    public void setMemHistogram(DecayingHistogram memHistogram) {
        this.memHistogram = memHistogram;
    }

    public String getJobVersion() {
        return jobVersion;
    }

    public void setJobVersion(String jobVersion) {
        this.jobVersion = jobVersion;
    }
}
