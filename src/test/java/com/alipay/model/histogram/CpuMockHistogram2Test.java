package com.alipay.model.histogram;

import com.alipay.model.metrics.BaseMetric;
import com.alipay.utils.FileUtil;
import com.alipay.utils.TimestampUtil;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.alipay.consts.HistogramConsts.defaultEpsilon;
import static com.alipay.consts.HistogramConsts.defaultHistogramBucketSizeGrowth;

/**
 * Mock with Cpu util data
 *
 * @author sansi.xy
 * @date 12/31/25
 */

public class CpuMockHistogram2Test {

    @Test
    public void buildCpuMemfile() throws IOException {
        // exp variable
        String cpu = "showdata2/cpuuse2.csv";
        String mem = "showdata2/memuse2.csv";
        String outputPath = "showdata2/resource2.csv";

        List<String> cpuValue = Lists.newArrayList();
        FileUtil.readFileWithBufferedReader(cpu, line -> {
            String[] split = line.split(",");
            Double value = Double.parseDouble(split[1].trim())/100;
            cpuValue.add(value.toString());
        }, true);

        List<String> newContent = Lists.newArrayList();
        try (BufferedReader reader = new BufferedReader(new FileReader(mem))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                if (lineNum++ == 1) {
                    continue;
                }

                if (StringUtils.isBlank(line)) {
                    continue;
                }
                try {
                    String[] split = line.split(",");
                    String time = split[0];
                    String memV = split[1];
                    Double v = Double.parseDouble(memV)/1024/1024;
                    String memInMB = Integer.valueOf(v.intValue()).toString();
                    String cpuV = cpuValue.get(lineNum);
                    newContent.add(time + "," + cpuV + "," + memInMB);
                } catch (Exception e) {
                    // skip it
                    System.out.println("Skip line " + line);
                }
            }
        } catch (IOException e) {
            throw new IOException("读取文件时发生错误: " + e.getMessage());
        }

        FileUtil.writeFileFromList(outputPath, newContent);
    }

    @Test
    public void testWithCpuHistogram() throws Exception {
        // exp variable
        String filePath = "showdata2/cpumock2.csv";
        String outputPath = "showdata2/cpumock2_mw_4_3.csv";
        long millis = Duration.ofMinutes(60).toMillis();
        int sampleWindow = 12;
        HistogramAggType type = HistogramAggType.Percentile;

        HistogramOptions cpuHistogramOptions = new HistogramOptions(14000, 1, 1 + defaultHistogramBucketSizeGrowth, defaultEpsilon);
        DecayingHistogram decayingHistogram = new DecayingHistogram(cpuHistogramOptions, millis,
                TimestampUtil.stringToTimestampMs("2025-12-29 11:10:00"));

        List<BaseMetric> metricsList = new ArrayList<>();
        FileUtil.readFileWithBufferedReader(filePath, line -> {
            String[] split = line.split(",");
            Long ts = TimestampUtil.stringToTimestampMs(split[0].trim());
            Double value = Double.parseDouble(split[1].trim());
            BaseMetric point = new BaseMetric("cpu_util", ts, value);
            metricsList.add(point);
        }, true);

        LinkedList<String> outputList = new LinkedList<>();
        outputList.addFirst("time,cpu_util");

        int lineNum = 0;
        for (BaseMetric baseMetric : metricsList) {
            lineNum++;
            if (lineNum % sampleWindow == 0) {
                decayingHistogram.addSample(baseMetric.getValue(), 1, baseMetric.getTimestamp());
                String str = TimestampUtil.timestampMsToString(baseMetric.getTimestamp());
                outputList.add(str
                        + ","
                        + decayingHistogram.agg(new HistogramAggPolicy(type, 0.95))
                );
            }
        }
        FileUtil.writeFileFromList(outputPath, outputList);
    }
}
