package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.OnEventHandler;

public class CpuCollector {

    private int count;
    private int lastPercent;

    @OnEventHandler
    public boolean onCpu(CpuMetric e) {
        count++;
        lastPercent = e.getPercent();
        return true;
    }

    public int getCount() { return count; }
    public int getLastPercent() { return lastPercent; }
}
