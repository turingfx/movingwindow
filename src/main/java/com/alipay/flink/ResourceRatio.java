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

/**
 * This class enables controlling pre-allocation or estimation of cpu and memory usage separately.
 * We assume that memory needs more strict limitation than cpu.
 */
public class ResourceRatio {
    private volatile double cpuRatio;
    private volatile double memoryRatio;

    public ResourceRatio(double cpuRatio, double memoryRatio) {
        this.cpuRatio = cpuRatio;
        this.memoryRatio = memoryRatio;
    }

    public double getCpuRatio() {
        return cpuRatio;
    }

    public double getMemoryRatio() {
        return memoryRatio;
    }

    public void setCpuRatio(double cpuRatio) {
        this.cpuRatio = cpuRatio;
    }

    public void setMemoryRatio(double memoryRatio) {
        this.memoryRatio = memoryRatio;
    }

    @Override
    public String toString() {
        return "ResourceRatio{" + "cpuRatio=" + cpuRatio + ", memoryRatio=" + memoryRatio + '}';
    }
}
