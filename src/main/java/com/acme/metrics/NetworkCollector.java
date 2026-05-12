package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.OnEventHandler;

public class NetworkCollector {

    private int count;
    private long lastTotalBytes;

    @OnEventHandler
    public boolean onNet(NetworkMetric e) {
        count++;
        lastTotalBytes = e.getTotalBytes();
        return true;
    }

    public int getCount() { return count; }
    public long getLastTotalBytes() { return lastTotalBytes; }
}
