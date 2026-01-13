package com.alipay.model.profiler;

import com.alipay.utils.DoubleUtil;

/**
 * @author sansi.xy
 * @date 12/31/25
 */
public class MarginAdjust {
  // inc ratio, eg 0.1, then value will increase 10%
  private double ratio;
  // inc min value per step
  private double minStep;
  // inc max value per step
  private double maxStep;

  public MarginAdjust(double ratio, double minStep, double maxStep) {
    this.ratio = ratio;
    this.minStep = minStep;
    this.maxStep = maxStep;
  }

  public double applyAdjust(double value) {
    double adjust = Math.max(DoubleUtil.multiply(value, ratio), minStep);
    if (maxStep > 0) {
      adjust = Math.min(adjust, maxStep);
    }
    return DoubleUtil.add(adjust, value);
  }

  public double getRatio() {
    return ratio;
  }

  public void setRatio(double ratio) {
    this.ratio = ratio;
  }

  public double getMinStep() {
    return minStep;
  }

  public void setMinStep(double minStep) {
    this.minStep = minStep;
  }

  public double getMaxStep() {
    return maxStep;
  }

  public void setMaxStep(double maxStep) {
    this.maxStep = maxStep;
  }
}
