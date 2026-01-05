package com.alipay.histogram;

/**
 * @author sansi.xy
 * @date 12/29/25
 */

public class HistogramAggPolicy {
    HistogramAggType type;
    double percentile;

    public HistogramAggPolicy(HistogramAggType type, double percentile) {
        this.type = type;
        this.percentile = percentile;
    }
}
