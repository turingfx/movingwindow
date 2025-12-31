package com.alipay.model.histogram;

import com.alipay.histogram.DecayingHistogram;
import com.alipay.histogram.HistogramOptions;
import com.alipay.metrics.BaseMetric;
import com.alipay.utils.FileUtil;
import com.alipay.utils.TimestampUtil;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
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
        String filePath = "data/cpumock2.csv";
        String outputPath = "data/cpumock2_mw.csv";

        HistogramOptions cpuHistogramOptions = HistogramOptions.getCpuHistogramOptions();
        DecayingHistogram decayingHistogram = new DecayingHistogram(cpuHistogramOptions, Duration.ofHours(1).toMillis(),
                TimestampUtil.stringToTimestampMs("2025-12-30 01:00:00"));

        List<BaseMetric> metricsList = new ArrayList<>();
        FileUtil.readFileWithBufferedReader(filePath, line -> {
            String[] split = line.split(",");
            Long ts = TimestampUtil.stringToTimestampMs(split[0].trim());
            Double value = Double.parseDouble(split[1].trim());
            BaseMetric point = new BaseMetric("cpu_util", ts, value);
            metricsList.add(point);
        });

        List<String> outputList = new ArrayList<>();
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
