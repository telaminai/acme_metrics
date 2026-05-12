package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.FluxtionDataOnly;

@FluxtionDataOnly
public class CpuMetric {
    private final String host;
    private final int percent;

    public CpuMetric(String host, int percent) {
        this.host = host;
        this.percent = percent;
    }

    public String getHost() { return host; }
    public int getPercent() { return percent; }

    @Override
    public String toString() { return "cpu{" + host + "=" + percent + "%}"; }
}
