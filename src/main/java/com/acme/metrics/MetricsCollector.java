package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.OnEventHandler;

public class MetricsCollector {

    private int count;
    private int lastValue;

    @OnEventHandler
    public boolean onMetric(MetricEvent event) {
        count++;
        lastValue = event.getValue();
        return true;
    }

    public int getCount() { return count; }
    public int getLastValue() { return lastValue; }
}
