package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.OnTrigger;

public class ThresholdAlerter {

    public static final int CPU_THRESHOLD = 80;
    public static final double RATIO_THRESHOLD = 0.85;

    private final MetricsAggregator aggregator;

    // Watermarks gate the alert checks so we only re-evaluate when the
    // matching collector actually fired; without them every aggregator
    // tick would re-raise the standing reading (cpu=90 stays 90 for the
    // rest of the run).
    private int lastCpuCount;
    private int lastMemCount;
    private int alertCount;
    private String lastReason = "";

    public ThresholdAlerter() {
        this(new MetricsAggregator());
    }

    public ThresholdAlerter(MetricsAggregator aggregator) {
        this.aggregator = aggregator;
    }

    // Returns true ONLY when a fresh alert is raised — suppresses
    // downstream AlertPublisher firing on benign updates and on
    // sustained-but-already-reported readings.
    @OnTrigger
    public boolean onAggregatorUpdate() {
        boolean raised = false;
        int cpuCount = aggregator.getCpu().getCount();
        if (cpuCount > lastCpuCount) {
            int cpuLast = aggregator.getCpu().getLastPercent();
            lastCpuCount = cpuCount;
            if (cpuLast > CPU_THRESHOLD) {
                alertCount++;
                lastReason = "cpu=" + cpuLast;
                raised = true;
            }
        }
        int memCount = aggregator.getMemory().getCount();
        if (memCount > lastMemCount) {
            double memLast = aggregator.getMemory().getLastRatio();
            lastMemCount = memCount;
            if (memLast > RATIO_THRESHOLD) {
                alertCount++;
                lastReason = "mem-ratio=" + memLast;
                raised = true;
            }
        }
        return raised;
    }

    public int getAlertCount() { return alertCount; }
    public String getLastReason() { return lastReason; }
    public MetricsAggregator getAggregator() { return aggregator; }
}
