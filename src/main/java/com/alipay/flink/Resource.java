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

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * 该类借鉴ResourceProfile，重新引入该类是因为ResourceProfile是slot级别，而我们需要的是进程级别的描述信息， 用于JobManager/TaskManager.
 * 比较大小顺序先Mem,后Cpu
 *
 * <ol>
 *   <li>Memory Size
 *   <li>CPU cores
 * </ol>
 */
public class Resource implements Serializable, Comparable<Resource> {

    private static final long serialVersionUID = 1L;

    /** Memory weight, Support custom setting later. */
    private static final float MEMORY_WEIGHT = 0.8f;

    public static final Resource UNKNOWN = new Resource(-1.0, -1);

    public static final Resource EMTPY = new Resource(0, 0);

    // ------------------------------------------------------------------------

    /** How many cpu cores are needed, use double so we can specify cpu like 0.1. */
    private double cpuCores;

    /** How many memory in mb. */
    private int memInMB;

    private volatile int hashResult;

    // ------------------------------------------------------------------------

    /**
     * Creates a new Resource.
     *
     * @param cpuCores The number of CPU cores (possibly fractional, i.e., 0.2 cores)
     * @param memInMB The size of the heap memory, in megabytes.
     */
    public Resource(double cpuCores, int memInMB) {
        this.cpuCores = cpuCores;
        this.memInMB = memInMB;
        updateHashCode();
    }

    /**
     * Creates a copy of the given Resource.
     *
     * @param other The Resource to copy.
     */
    public Resource(Resource other) {
        this(other.cpuCores, other.memInMB);
    }

    // ------------------------------------------------------------------------

    /**
     * Get the cpu cores needed.
     *
     * @return The cpu cores, 1.0 means a full cpu thread
     */
    public double getCpuCores() {
        return cpuCores;
    }

    /**
     * Get the memory in MB.
     *
     * @return The memory in MB
     */
    public int getMemInMB() {
        return memInMB;
    }

    /**
     * Check whether required resource profile can be matched.
     *
     * @param required the required resource profile
     * @return true if the requirement is matched, otherwise false
     */
    public boolean isMatching(Resource required) {

        if (cpuCores >= required.getCpuCores() && memInMB >= required.getMemInMB()) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(@Nonnull Resource other) {
        int cmp = Double.compare(this.weighted(), other.weighted());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(this.getMemInMB(), other.getMemInMB());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Double.compare(this.cpuCores, other.cpuCores);
        return cmp;
    }

    public double weighted() {
        return cpuCores * (1 - MEMORY_WEIGHT) + memInMB / 1024f * MEMORY_WEIGHT;
    }

    public boolean isOver(Resource another) {
        return cpuCores >= another.cpuCores && memInMB >= another.memInMB;
    }

    public Resource minus(Resource another) {
        return new Resource(cpuCores - another.getCpuCores(), memInMB - another.getMemInMB());
    }

    public Resource merge(Resource another) {
        Resource resource = new Resource(this);
        resource.addTo(another);
        return resource;
    }

    public void addTo(Resource another) {
        this.cpuCores += another.getCpuCores();
        this.memInMB += another.getMemInMB();
        updateHashCode();
    }

    public void cutOff(Resource another) {
        this.cpuCores -= another.getCpuCores();
        this.memInMB -= another.getMemInMB();
        updateHashCode();
    }

    public void updateToMaxOf(Resource another) {
        this.cpuCores = Math.max(this.cpuCores, another.cpuCores);
        this.memInMB = Math.max(this.memInMB, another.memInMB);
        updateHashCode();
    }

    public void updateToMinOf(Resource another) {
        this.cpuCores = Math.min(this.cpuCores, another.cpuCores);
        this.memInMB = Math.min(this.memInMB, another.memInMB);
        updateHashCode();
    }

    public Resource multiply(int multiplier) {
        return new Resource(this.getCpuCores() * multiplier, this.getMemInMB() * multiplier);
    }

    public Resource multiply(double multiplier) {
        return new Resource(
                this.getCpuCores() * multiplier, (int) (this.getMemInMB() * multiplier));
    }

    public Resource multiply(ResourceRatio ratio) {
        return new Resource(
                this.getCpuCores() * ratio.getCpuRatio(),
                (int) (this.getMemInMB() * ratio.getMemoryRatio()));
    }

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return hashResult;
    }

    private void updateHashCode() {
        final long cpuBits = Double.doubleToLongBits(cpuCores);
        int result = (int) (cpuBits ^ (cpuBits >>> 32));
        result = 31 * result + memInMB;
        this.hashResult = result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == Resource.class) {
            Resource that = (Resource) obj;
            if (this.hashCode() != that.hashCode()) {
                return false;
            }
            return this.cpuCores == that.getCpuCores() && this.memInMB == that.getMemInMB();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Resource{" + "cpuCores=" + cpuCores + ", memInMB=" + memInMB + '}';
    }
}
