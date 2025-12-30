package com.alipay.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleUtil {
    private static final int DEFAULT_SCALE = 4;
    
    public static double add(double a, double b) {
        return round(a + b);
    }
    
    public static double subtract(double a, double b) {
        return round(a - b);
    }
    
    public static double multiply(double a, double b) {
        return round(a * b);
    }
    
    public static double divide(double a, double b) {
        return round(a / b);
    }
    
    public static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        return bd.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP).doubleValue();
    }
}