package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.OnTrigger;

public class WindowedAverager {

    private final MetricsAggregator aggregator;

    private int lastCpuCount;
    private int sampleCount;
    private long cpuSum;
    private double avgCpu;

    public WindowedAverager() {
        this(new MetricsAggregator());
    }

    public WindowedAverager(MetricsAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @OnTrigger
    public boolean onAggregatorUpdate() {
        int curCount = aggregator.getCpu().getCount();
        if (curCount > lastCpuCount) {
            sampleCount++;
            cpuSum += aggregator.getCpu().getLastPercent();
            avgCpu = (double) cpuSum / sampleCount;
            lastCpuCount = curCount;
            return true;
        }
        return false;
    }

    public double getAvgCpu() { return avgCpu; }
    public int getSampleCount() { return sampleCount; }
    public MetricsAggregator getAggregator() { return aggregator; }
}
