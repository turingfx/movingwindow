package com.alipay.conf;

/**
 * @author sansi.xy
 * @date 12/31/25
 */
public class MarginAdjust {
  private double ratio;
  private double minStep;
  private double maxStep;

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
