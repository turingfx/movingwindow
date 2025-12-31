package com.alipay.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 时间戳转换工具类测试
 */
public class TimestampUtilTest {
    
    @Test
    public void testStringToTimestamp() {
        String dateTimeStr = "2025-12-30 08:13:00";
        long expectedTimestamp = 1767053580_000L; // 2025-12-30 08:13:00 对应的时间戳
        
        long actualTimestamp = TimestampUtil.stringToTimestampMs(dateTimeStr);

        assertEquals(expectedTimestamp,actualTimestamp);
    }
    
    @Test
    public void testStringToTimestampMs() {
        String dateTimeStr = "2025-12-30 08:13:00";
        long actualTimestampMs = TimestampUtil.stringToTimestampMs(dateTimeStr);
        
        assertTrue("毫秒时间戳转换不应返回-1", actualTimestampMs != -1);
    }
    
    @Test
    public void testTimestampToString() {
        long expectedTimestamp = 1767053580_000L; // 2025-12-30 08:13:00 对应的时间戳

        String result = TimestampUtil.timestampMsToString(expectedTimestamp);

        assertEquals(result,"2025-12-30 08:13:00");
    }
}