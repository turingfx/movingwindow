package com.alipay.model.histogram;

import com.alipay.model.metrics.BaseMetric;
import com.alipay.utils.FileUtil;
import com.alipay.utils.TimestampUtil;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.alipay.consts.HistogramConsts.defaultEpsilon;
import static com.alipay.consts.HistogramConsts.defaultHistogramBucketSizeGrowth;

/**
 * Mock with Cpu util data
 *
 * @author sansi.xy
 * @date 12/31/25
 */

public class MemMockHistogram2Test {

    public static final double PERCENTILE = 0.6;

    private Executor executor = Executors.newFixedThreadPool(1);

    @Test
    public void testWithMemHistogram() throws Exception {
        long start = System.currentTimeMillis();
        // exp variable
        String filePath = "showdata2/memmock.csv";
        String outputPath = "showdata2/memmock_mw_12h_2.csv";
        long millis = Duration.ofHours(12).toMillis();
        int sampleWindow = 12;
        HistogramAggType type = HistogramAggType.Percentile;

        HistogramOptions cpuHistogramOptions = new HistogramOptions(1e7, 10, 1 + defaultHistogramBucketSizeGrowth, defaultEpsilon);
        DecayingHistogram decayingHistogram = new DecayingHistogram(cpuHistogramOptions, millis,
                TimestampUtil.stringToTimestampMs("2026-01-01 10:45:00"));

        List<BaseMetric> metricsList = new ArrayList<>();
        FileUtil.readFileWithBufferedReader(filePath, line -> {
            String[] split = line.split(",");
            Long ts = TimestampUtil.stringToTimestampMs(split[0].trim());
            Double value = Double.parseDouble(split[1].trim());
            BaseMetric point = new BaseMetric("mem_used", ts, value/(1024*1024));
            // multi thread exec
            executor.execute(() -> metricsList.add(point));
        }, true);

        LinkedList<String> outputList = new LinkedList<>();
        outputList.addFirst("time,mem_used");

        int lineNum = 0;
        for (BaseMetric baseMetric : metricsList) {
            lineNum++;
            if (lineNum % sampleWindow == 0) {
                decayingHistogram.addSample(baseMetric.getValue(), 1, baseMetric.getTimestamp());
                String str = TimestampUtil.timestampMsToString(baseMetric.getTimestamp());
                double profileMem = decayingHistogram.agg(new HistogramAggPolicy(type, PERCENTILE)) * 1024 * 1024;
                outputList.add(str
                        + ","
                        + profileMem);
            }
        }
        FileUtil.writeFileFromList(outputPath, outputList);

        long end = System.currentTimeMillis();
        long l = end - start;
        System.out.println("size: "+ lineNum +",cost:" + l +" ,avg:" + (double)l/lineNum);

    }
}
