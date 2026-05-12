package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.OnTrigger;

public class MetricsAggregator {

    private final MetricsCollector collector;
    private int runningTotal;

    // No-arg ctor for ease-of-use authoring tools (Spring XML can name
    // this bean without wiring up a collector explicitly). Internally
    // creates the collector and delegates to the field-matching ctor
    // below. The internal collector becomes a transitive node when
    // Fluxtion source-gen walks the field graph.
    public MetricsAggregator() {
        this(new MetricsCollector());
    }

    // Field-matching ctor — this is the one Fluxtion source-gen picks
    // when emitting reconstruction code for the generated processor.
    public MetricsAggregator(MetricsCollector collector) {
        this.collector = collector;
    }

    public MetricsCollector getCollector() { return collector; }

    @OnTrigger
    public boolean onCollectorUpdate() {
        runningTotal += collector.getLastValue();
        return true;
    }

    public int getRunningTotal() { return runningTotal; }
}
