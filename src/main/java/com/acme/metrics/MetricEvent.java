package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.FluxtionDataOnly;

@FluxtionDataOnly
public class MetricEvent {
    private final String name;
    private final int value;

    public MetricEvent(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public int getValue() { return value; }

    @Override
    public String toString() { return name + "=" + value; }
}
