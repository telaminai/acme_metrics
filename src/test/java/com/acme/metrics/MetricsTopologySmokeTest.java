package com.acme.metrics;

import com.telamin.fluxtion.builder.extern.spring.FluxtionSpring;
import com.telamin.fluxtion.runtime.DataFlow;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.*;

class MetricsTopologySmokeTest {

    @Test
    void springXmlDrivesAllThreeEventTypes_subgraphMaterialisesViaNoArgCtor() throws Exception {
        GenericApplicationContext ctx = new GenericApplicationContext();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ctx);
        reader.setValidating(false);
        reader.setNamespaceAware(false);
        reader.loadBeanDefinitions(new ClassPathResource("application-context.xml"));
        ctx.refresh();

        // In-process compile via FluxtionSpring — what the playground
        // calls via FluxtionSpring.applyContext in the AOT builder.
        DataFlow flow = FluxtionSpring.compile(ctx);
        flow.init();
        flow.start();

        // Drive all three event types. Mix of below-threshold and
        // above-threshold values so we exercise both the normal path
        // and the alert path.
        flow.onEvent(new CpuMetric("host-a", 30));     // below cpu threshold (80)
        flow.onEvent(new MemoryMetric("host-a", 0.60));// below mem threshold (0.85)
        flow.onEvent(new NetworkMetric("host-a", 1_000));
        flow.onEvent(new CpuMetric("host-b", 90));     // ALERT — cpu > 80
        flow.onEvent(new MemoryMetric("host-b", 0.95));// ALERT — mem > 0.85
        flow.onEvent(new NetworkMetric("host-b", 2_500));
        flow.onEvent(new CpuMetric("host-c", 45));     // below

        // Resolve the bean instances Fluxtion is now driving. Each bean
        // id in XML becomes a node id in the generated processor; we
        // look them up to assert state.
        MetricsAggregator agg = (MetricsAggregator) flow.getNodeById("aggregator");
        WindowedAverager avg = (WindowedAverager) flow.getNodeById("averager");
        ThresholdAlerter alerter = (ThresholdAlerter) flow.getNodeById("alerter");

        // Each collector should have observed exactly its own events:
        // 3 cpu, 2 memory, 2 network.
        assertEquals(3, agg.getCpu().getCount(), "cpu collector count");
        assertEquals(2, agg.getMemory().getCount(), "memory collector count");
        assertEquals(2, agg.getNetwork().getCount(), "network collector count");

        // Aggregator running stats (watermark gating means each
        // stat updates only when the matching collector fires).
        assertEquals(30 + 90 + 45, agg.getRunningCpuTotal(), "running cpu total");
        assertEquals(0.95, agg.getPeakRatio(), 1e-9, "peak memory ratio");
        assertEquals(3_500, agg.getTotalNetworkBytes(), "total network bytes");

        // Averager: 3 cpu samples, avg = (30+90+45)/3.
        assertEquals(3, avg.getSampleCount(), "averager sample count");
        assertEquals((30.0 + 90.0 + 45.0) / 3, avg.getAvgCpu(), 1e-9, "avg cpu");

        // Alerter: cpu=90 raised one alert, mem=0.95 raised another.
        assertEquals(2, alerter.getAlertCount(), "alert count");
    }
}
