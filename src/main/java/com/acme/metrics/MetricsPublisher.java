package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.OnTrigger;

public class MetricsPublisher {

    private final MetricsAggregator aggregator;

    public MetricsPublisher(MetricsAggregator aggregator) {
        this.aggregator = aggregator;
    }

    public MetricsAggregator getAggregator() { return aggregator; }

    @OnTrigger
    public boolean onAggregatorUpdate() {
        System.out.println("[acme-metrics] count="
                + aggregator.getCollector().getCount()
                + " total=" + aggregator.getRunningTotal());
        return true;
    }
}
