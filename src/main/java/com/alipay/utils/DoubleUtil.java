package com.alipay.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleUtil {
  private static final int DEFAULT_SCALE = 4;
  private static final double THRESHOLD = 0.00001;

  public static double add(double a, double b) {
    BigDecimal ba = BigDecimal.valueOf(a);
    BigDecimal bb = BigDecimal.valueOf(b);
    double v = ba.add(bb).doubleValue();
    return round(v);
  }

  public static double subtract(double a, double b) {
    BigDecimal ba = BigDecimal.valueOf(a);
    BigDecimal bb = BigDecimal.valueOf(b);
    double v = ba.subtract(bb).doubleValue();
    return round(v);
  }

  public static double multiply(double a, double b) {
    BigDecimal ba = BigDecimal.valueOf(a);
    BigDecimal bb = BigDecimal.valueOf(b);
    double v = ba.multiply(bb).doubleValue();
    return round(v);
  }

  public static double divide(double a, double b) {
    BigDecimal ba = BigDecimal.valueOf(a);
    BigDecimal bb = BigDecimal.valueOf(b);
      return ba.divide(bb, DEFAULT_SCALE, RoundingMode.HALF_UP).doubleValue();
  }

  public static double round(double value) {
    BigDecimal bd = BigDecimal.valueOf(value);
    return bd.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP).doubleValue();
  }

  public static boolean equal(double a, double b) {
    return Math.abs(a - b) < THRESHOLD;
  }

}
