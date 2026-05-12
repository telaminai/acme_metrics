# metrics-nodes

A sample third-party Fluxtion node library — bundles a small but
realistic metrics-ingestion subgraph that an integrator can pull into
their own application via Spring XML.

```
groupId:    com.acme.metrics
artifactId: metrics-nodes
version:    1.1-SNAPSHOT
```

The library has no Spring dependency itself — it ships plain Fluxtion
nodes and event types. Spring is the integrator's choice. `fluxtion-runtime`
is `provided` scope so consumers (the playground, real apps) keep their
own runtime version on the classpath.

## What's inside

3 event types:

| Event | Fields |
|---|---|
| `CpuMetric` | `host`, `percent` (int) |
| `MemoryMetric` | `host`, `usedRatio` (double) |
| `NetworkMetric` | `host`, `totalBytes` (long) |

8 Fluxtion nodes:

| Node | Role | Trigger |
|---|---|---|
| `CpuCollector` | counts + holds latest CPU reading | `@OnEventHandler CpuMetric` |
| `MemoryCollector` | counts + holds latest memory ratio | `@OnEventHandler MemoryMetric` |
| `NetworkCollector` | counts + holds latest byte total | `@OnEventHandler NetworkMetric` |
| `MetricsAggregator` | running CPU total, peak mem ratio, total bytes | `@OnTrigger` on any collector |
| `WindowedAverager` | running CPU average across all samples | `@OnTrigger` on aggregator |
| `ThresholdAlerter` | raises an alert when CPU > 80 or mem-ratio > 0.85 | `@OnTrigger` on aggregator |
| `MetricsPublisher` | prints aggregated state | `@OnTrigger` on aggregator + averager |
| `AlertPublisher` | prints fresh alerts | `@OnTrigger` on alerter |

## The two-ctor vendor contract

Every node ships **two** constructors:

1. **A no-arg ctor** — for ease-of-use authoring tools (Spring XML can
   reference the bean without manually wiring up its dependencies).
   Internally it instantiates dependent nodes and delegates.
2. **A field-matching ctor** — Fluxtion source-gen picks this one when
   emitting reconstruction code. Parameters map onto the renderable
   fields one-for-one.

For example, `MetricsAggregator`:

```java
public MetricsAggregator() {
    this(new CpuCollector(), new MemoryCollector(), new NetworkCollector());
}

public MetricsAggregator(CpuCollector cpu,
                         MemoryCollector memory,
                         NetworkCollector network) {
    this.cpu = cpu;
    this.memory = memory;
    this.network = network;
}
```

When Spring instantiates `aggregator` via the no-arg ctor, three
collectors become private fields on it. `FluxtionSpring.applyContext`
walks the bean registry and adds the aggregator as a node; Fluxtion
source-gen then walks the **field references** and discovers the three
collectors transitively. The generated processor contains three
auto-id'd collector nodes despite XML never naming them.

## Using it from Spring XML

```xml
<bean id="aggregator" class="com.acme.metrics.MetricsAggregator"/>

<bean id="averager" class="com.acme.metrics.WindowedAverager">
    <constructor-arg ref="aggregator"/>
</bean>

<bean id="alerter" class="com.acme.metrics.ThresholdAlerter">
    <constructor-arg ref="aggregator"/>
</bean>

<bean id="publisher" class="com.acme.metrics.MetricsPublisher">
    <constructor-arg ref="aggregator"/>
    <constructor-arg ref="averager"/>
</bean>

<bean id="alertPublisher" class="com.acme.metrics.AlertPublisher">
    <constructor-arg ref="alerter"/>
</bean>
```

Five beans in XML → eight nodes in the generated processor. The three
collectors come from the aggregator's no-arg ctor.

## Build & test

```sh
mvn test     # runs MetricsTopologySmokeTest — exercises the full
             # topology in-process via FluxtionSpring.compile and asserts
             # state across all 8 nodes
mvn package  # produces target/metrics-nodes-<version>.jar
```

`MetricsTopologySmokeTest` is the reference for the expected behaviour
of the library: it drives 3 cpu + 2 mem + 2 net events through the
generated processor and asserts the running totals, peak ratio,
windowed average, and alert count.

## Where it's used

- **Playground / fluxtion-web** — the *Spring builder — 3rd-party*
  example pulls this jar as a dep via the playground catalog and
  demonstrates the AOT build path end-to-end. The JAR is mirrored to
  [`fluxtion-playground-libs`](https://github.com/telaminai/fluxtion-playground-libs)
  for browser fetching.
