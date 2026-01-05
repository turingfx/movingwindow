package com.alipay.model.profiler;

import com.alipay.model.histogram.DecayingHistogram;

/**
 * @author sansi.xy
 * @date 1/5/26
 */

public class MovingWindowState {
    DecayingHistogram cpuHistogram;
    DecayingHistogram memHistogram;
    String jobVersion;

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
