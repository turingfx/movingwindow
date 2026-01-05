package com.alipay.model.histogram;

import com.alipay.model.metrics.BaseMetric;
import com.alipay.utils.FileUtil;
import com.alipay.utils.TimestampUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.alipay.model.consts.HistogramConsts.defaultEpsilon;
import static com.alipay.model.consts.HistogramConsts.defaultHistogramBucketSizeGrowth;

/**
 * Mock with Cpu util data
 *
 * @author sansi.xy
 * @date 12/31/25
 */

public class CpuMockSimpleAvgTest {

    @Test
    public void testWithCpuHistogram() throws Exception {
        // exp variable
        String filePath = "showdata2/cpumock2.csv";
        String outputPath = "showdata2/cpumock2_mw_5.csv";
        int sampleWindow = 12;
        HistogramAggType type = HistogramAggType.AVERAGE;

        HistogramOptions cpuHistogramOptions = new HistogramOptions(14000, 1, 1 + defaultHistogramBucketSizeGrowth, defaultEpsilon);
        Histogram histogram = new Histogram(cpuHistogramOptions);

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
                histogram.addSample(baseMetric.getValue(), 1, baseMetric.getTimestamp());
                String str = TimestampUtil.timestampMsToString(baseMetric.getTimestamp());
                outputList.add(str
                        + ","
                        + histogram.agg(new HistogramAggPolicy(type, 0.95))
                );
            }
        }
        FileUtil.writeFileFromList(outputPath, outputList);
    }
}
