package com.alipay.flink.profiler;

import com.alipay.conf.WMProfilerConfig;
import com.alipay.flink.HistogramConst;
import com.alipay.flink.Resource;
import com.alipay.flink.ResourceWithTime;
import com.alipay.model.histogram.DecayingHistogram;
import com.alipay.model.histogram.HistogramOptions;
import com.alipay.utils.FileUtil;
import com.alipay.utils.TimestampUtil;
import org.apache.commons.compress.utils.Lists;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.alipay.consts.HistogramConsts.defaultEpsilon;
import static com.alipay.consts.HistogramConsts.defaultHistogramBucketSizeGrowth;

public class FlinkWMProfilerTest {

    FlinkWMProfiler profiler;

    @Before
    public void init() throws Exception {

        HistogramOptions cpuHistogramOptions = new HistogramOptions(14000, 1, 1 + defaultHistogramBucketSizeGrowth, defaultEpsilon);
        HistogramOptions memHistogramOptions = new HistogramOptions(1e7, 10, 1 + defaultHistogramBucketSizeGrowth, defaultEpsilon);
        long millis = Duration.ofHours(6).toMillis();


        profiler = new FlinkWMProfiler(WMProfilerConfig.getDefault(), "default");
        profiler.getState().setCpuHistogram(new DecayingHistogram(
                cpuHistogramOptions, HistogramConst.DEFAULT_CPU_HALF_LIFE_MS));
        profiler.getState().setMemHistogram(new DecayingHistogram(
                memHistogramOptions, millis));
    }

    @Test
    public void testProfile() throws IOException {
        String filePath = "showdata2/resource2.csv";
        String outputPath = "showdata2/resource2_mv.csv";

        List<ResourceWithTime> metricsList = new ArrayList<>();
        FileUtil.readFileWithBufferedReader(filePath, line -> {
            String[] split = line.split(",");
            Long ts = TimestampUtil.stringToTimestampMs(split[0].trim());
            Double cpu = Double.parseDouble(split[1].trim());
            Integer mem = Integer.parseInt(split[2].trim());

            Resource resource = new Resource(cpu, mem);
            metricsList.add(new ResourceWithTime(resource,ts));
        }, true);

        List<String> outPut = Lists.newArrayList();
        for (ResourceWithTime resourceWithTime : metricsList) {
            profiler.feedData(resourceWithTime.getResource(),resourceWithTime.getTime());
            Optional<Resource> resource = profiler.fetchProfile();
            Resource profile = resource.get();
            outPut.add(TimestampUtil.timestampMsToString(resourceWithTime.getTime())+","+ profile.getCpuCores() + "," + profile.getMemInMB());
        }

        FileUtil.writeFileFromList(outputPath,outPut);

        Resource resource = profiler.fetchProfile().get();
        System.out.println(resource.getCpuCores() + "," + resource.getMemInMB());
    }
}