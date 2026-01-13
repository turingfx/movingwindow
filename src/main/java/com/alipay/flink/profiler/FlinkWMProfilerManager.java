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
import com.alipay.flink.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Manager for {@link FlinkWMProfiler}, offer "thread safe","fo" etc. mechanism. */
public class FlinkWMProfilerManager {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkWMProfilerManager.class);
    private static final long PROFILE_GENERATOR_INTERVAL_MS = /*5 min*/ 300_000;
    private static final long PROFILE_GENERATOR_INIT_DELAY_MS = /*5 min*/ 300_000;

    private FlinkWMProfiler profiler;
    private ScheduledExecutorService profileGenerator;

    private final WMProfilerConfig profilerConfig;
    private final String                              profileKey;
    // profile is no need to generate so frequently, so we cache the profile and update it with
    // period
    private final AtomicReference<Optional<Resource>> profileResourceCached =
            new AtomicReference<>();
    private final ReentrantReadWriteLock              lock;

    public FlinkWMProfilerManager(WMProfilerConfig config, String profileKey) {
        this.profileKey = profileKey;
        this.profilerConfig = config;

        this.profiler = new FlinkWMProfiler(config, profileKey);
        this.lock = new ReentrantReadWriteLock();
        profileGenerator =
                Executors.newScheduledThreadPool(
                        1);
        this.profileGenerator.scheduleAtFixedRate(
                this::updateCached,
                PROFILE_GENERATOR_INIT_DELAY_MS,
                PROFILE_GENERATOR_INTERVAL_MS,
                TimeUnit.MILLISECONDS);
    }

    private void updateCached() {
        try {
            lock.readLock().lock();
            Optional<Resource> newValue = profiler.fetchProfile();
            LOG.debug("Gen new profile {}, at {}", newValue, System.currentTimeMillis());
            profileResourceCached.set(newValue);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<Resource> getProfile() {
        try {
            lock.readLock().lock();
            Optional<Resource> cachedData = profileResourceCached.get();
            return cachedData == null ? Optional.empty() : cachedData;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void profile(Resource resource, Long time) {
        try {
            lock.writeLock().lock();
            profiler.feedData(resource, time);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void mergeProfiler(FlinkWMProfiler other) {
        try {
            lock.writeLock().lock();
            profiler.mergeProfiler(other);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void reset() {
        try {
            lock.writeLock().lock();
            profiler = new FlinkWMProfiler(profilerConfig, profileKey);
            LOG.debug("Profiler with key {} is reset, config is {}", profileKey, profilerConfig);

            // rebuild scheduler
            profileGenerator.shutdownNow();

            // Create a new scheduled executor service
            ScheduledExecutorService newProfileGenerator =
                    Executors.newScheduledThreadPool(
                            1);

            newProfileGenerator.scheduleAtFixedRate(
                    this::updateCached,
                    PROFILE_GENERATOR_INIT_DELAY_MS,
                    PROFILE_GENERATOR_INTERVAL_MS,
                    TimeUnit.MILLISECONDS);

            // Update the reference to the new executor
            this.profileGenerator = newProfileGenerator;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // todo recover from stateful state with profileKey
}
