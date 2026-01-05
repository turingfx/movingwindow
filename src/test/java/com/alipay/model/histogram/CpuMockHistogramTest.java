package com.alipay.model.histogram;

import com.alipay.histogram.DecayingHistogram;
import com.alipay.histogram.HistogramOptions;
import com.alipay.metrics.BaseMetric;
import com.alipay.utils.FileUtil;
import com.alipay.utils.TimestampUtil;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Mock with Cpu util data
 *
 * @author sansi.xy
 * @date 12/31/25
 */

public class CpuMockHistogramTest {

    @Test
    public void testWithCpuHistogram() throws Exception {
        // exp variable
        String filePath = "showdata/cpumock2.csv";
        String outputPath = "showdata/cpumock2_mw_5.csv";
        long millis = Duration.ofMinutes(30).toMillis();

        HistogramOptions cpuHistogramOptions = HistogramOptions.getCpuHistogramOptions();
        DecayingHistogram decayingHistogram = new DecayingHistogram(cpuHistogramOptions, millis,
                TimestampUtil.stringToTimestampMs("2025-12-30 01:00:00"));

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

        for (BaseMetric baseMetric : metricsList) {
            decayingHistogram.addSample(baseMetric.getValue(), 1, baseMetric.getTimestamp());
            String str = TimestampUtil.timestampMsToString(baseMetric.getTimestamp());
            outputList.add(str
                    + ","
                    + decayingHistogram.average()
            );

            FileUtil.writeFileFromList(outputPath, outputList);
        }
    }
}
