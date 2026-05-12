package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.FluxtionDataOnly;

@FluxtionDataOnly
public class NetworkMetric {
    private final String host;
    private final long totalBytes;

    public NetworkMetric(String host, long totalBytes) {
        this.host = host;
        this.totalBytes = totalBytes;
    }

    public String getHost() { return host; }
    public long getTotalBytes() { return totalBytes; }

    @Override
    public String toString() { return "net{" + host + "=" + totalBytes + "b}"; }
}
