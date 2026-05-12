package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.OnTrigger;

public class MetricsPublisher {

    private final MetricsAggregator aggregator;
    private final WindowedAverager averager;

    public MetricsPublisher() {
        this(new MetricsAggregator(), new WindowedAverager());
    }

    public MetricsPublisher(MetricsAggregator aggregator, WindowedAverager averager) {
        this.aggregator = aggregator;
        this.averager = averager;
    }

    @OnTrigger
    public boolean onUpdate() {
        System.out.println("[acme-metrics] cpu-sum=" + aggregator.getRunningCpuTotal()
                + " peak-mem-ratio=" + aggregator.getPeakRatio()
                + " net-bytes=" + aggregator.getTotalNetworkBytes()
                + " avg-cpu=" + averager.getAvgCpu());
        return true;
    }

    public MetricsAggregator getAggregator() { return aggregator; }
    public WindowedAverager getAverager() { return averager; }
}
