package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.OnTrigger;

public class MetricsAggregator {

    private final CpuCollector cpu;
    private final MemoryCollector memory;
    private final NetworkCollector network;

    // Watermark counts so each running-stat only updates when the
    // corresponding collector actually fired. Without this, the
    // aggregator's @OnTrigger (fires on ANY parent update) would
    // re-add the latest cpu reading every time memory or network
    // fired, over-counting.
    private int lastCpuCount;
    private int lastMemCount;
    private int lastNetCount;

    private int runningCpuTotal;
    private double peakRatio;
    private long totalNetworkBytes;

    // No-arg ctor — root of the upstream subgraph. Builds the three
    // collectors internally so the integrator's Spring XML can declare
    // just `<bean id="aggregator" class="MetricsAggregator"/>` and have
    // all three collectors materialise as transitive nodes in the
    // generated processor.
    public MetricsAggregator() {
        this(new CpuCollector(), new MemoryCollector(), new NetworkCollector());
    }

    // Field-matching ctor for Fluxtion source-gen.
    public MetricsAggregator(CpuCollector cpu, MemoryCollector memory, NetworkCollector network) {
        this.cpu = cpu;
        this.memory = memory;
        this.network = network;
    }

    @OnTrigger
    public boolean onAnyUpdate() {
        if (cpu.getCount() > lastCpuCount) {
            runningCpuTotal += cpu.getLastPercent();
            lastCpuCount = cpu.getCount();
        }
        if (memory.getCount() > lastMemCount) {
            if (memory.getLastRatio() > peakRatio) peakRatio = memory.getLastRatio();
            lastMemCount = memory.getCount();
        }
        if (network.getCount() > lastNetCount) {
            totalNetworkBytes += network.getLastTotalBytes();
            lastNetCount = network.getCount();
        }
        return true;
    }

    public CpuCollector getCpu() { return cpu; }
    public MemoryCollector getMemory() { return memory; }
    public NetworkCollector getNetwork() { return network; }

    public int getRunningCpuTotal() { return runningCpuTotal; }
    public double getPeakRatio() { return peakRatio; }
    public long getTotalNetworkBytes() { return totalNetworkBytes; }
}
