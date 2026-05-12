package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.FluxtionDataOnly;

@FluxtionDataOnly
public class MemoryMetric {
    private final String host;
    private final double usedRatio;

    public MemoryMetric(String host, double usedRatio) {
        this.host = host;
        this.usedRatio = usedRatio;
    }

    public String getHost() { return host; }
    public double getUsedRatio() { return usedRatio; }

    @Override
    public String toString() { return "mem{" + host + "=" + usedRatio + "}"; }
}
