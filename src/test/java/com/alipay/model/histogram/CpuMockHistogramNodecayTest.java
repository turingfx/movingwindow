package com.alipay.model.histogram;

import com.alipay.model.metrics.BaseMetric;
import com.alipay.utils.AlgorithmUtil;
import com.alipay.utils.FileUtil;
import com.alipay.utils.TimestampUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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

public class CpuMockHistogramNodecayTest {

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

    @Test
    public void testWithSingleSliding() throws Exception{
        // exp variable
        String filePath = "showdata2/cpumock2.csv";
        String outputPath = "showdata2/cpumock2_mw_6.csv";
        int sampleWindow = 12;
        HistogramAggType type = HistogramAggType.AVERAGE;

        List<BaseMetric> metricsList = new ArrayList<>();
        FileUtil.readFileWithBufferedReader(filePath, line -> {
            String[] split = line.split(",");
            Long ts = TimestampUtil.stringToTimestampMs(split[0].trim());
            Double value = Double.parseDouble(split[1].trim());
            BaseMetric point = new BaseMetric("cpu_util", ts, value);
            metricsList.add(point);
        }, true);

        List<Double> originValues = metricsList.stream().map(BaseMetric::getValue).toList();
        double[] perHourValues = AlgorithmUtil.averageByChunk(originValues,20).stream().mapToDouble(Double::doubleValue).toArray();
        double[] calValues = AlgorithmUtil.averageSlidingWindow(perHourValues, 12);// Support half day to cal avg, pedding is 1h
        System.out.println(Arrays.toString(calValues));

        LinkedList<String> outputList = new LinkedList<>();
        outputList.addFirst("time,cpu_util");
        //
        //int lineNum = 0;
        //for (BaseMetric baseMetric : metricsList) {
        //    lineNum++;
        //    if (lineNum % sampleWindow == 0) {
        //    }
        //}
        //FileUtil.writeFileFromList(outputPath, outputList);
    }

    @Test
    public void testSlidingUtils() {
        double[] values = new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        double[] rs = AlgorithmUtil.averageSlidingWindow(values, 5);
        System.out.println(Arrays.toString(rs));
    }
}
