package com.alipay.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 时间戳转换工具类
 * 提供字符串日期和时间戳之间的转换功能
 */
public class TimestampUtil {
    
    // 常用日期格式
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_MS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    
    /**
     * 将日期时间字符串转换为时间戳(毫秒)
     * @param dateTimeStr 日期时间字符串，格式为 "yyyy-MM-dd HH:mm:ss"
     * @return 时间戳(毫秒)，转换失败返回-1
     */
    public static long stringToTimestampMs(String dateTimeStr) {
        return stringToTimestampMs(dateTimeStr, DEFAULT_DATETIME_FORMAT);
    }
    
    /**
     * 将日期时间字符串转换为时间戳(毫秒)
     * @param dateTimeStr 日期时间字符串
     * @param format 日期格式
     * @return 时间戳(毫秒)，转换失败返回-1
     */
    public static long stringToTimestampMs(String dateTimeStr, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr.trim(), formatter);
            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            return instant.toEpochMilli();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Parse ["+ dateTimeStr +"] error: " + e.getMessage());
        }
    }
    
    /**
     * 将毫秒时间戳转换为日期时间字符串
     * @param timestampMs 毫秒时间戳
     * @return 日期时间字符串，格式为 "yyyy-MM-dd HH:mm:ss"
     */
    public static String timestampMsToString(long timestampMs) {
        return timestampMsToString(timestampMs, DEFAULT_DATETIME_FORMAT);
    }
    
    /**
     * 将毫秒时间戳转换为日期时间字符串
     * @param timestampMs 毫秒时间戳
     * @param format 日期格式
     * @return 日期时间字符串
     */
    public static String timestampMsToString(long timestampMs, String format) {
        Instant instant = Instant.ofEpochMilli(timestampMs);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }
}