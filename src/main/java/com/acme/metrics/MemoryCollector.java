package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.OnEventHandler;

public class MemoryCollector {

    private int count;
    // double, not float — float fields trip CheerpJ Unsafe.getFloatVolatile.
    private double lastRatio;

    @OnEventHandler
    public boolean onMemory(MemoryMetric e) {
        count++;
        lastRatio = e.getUsedRatio();
        return true;
    }

    public int getCount() { return count; }
    public double getLastRatio() { return lastRatio; }
}
