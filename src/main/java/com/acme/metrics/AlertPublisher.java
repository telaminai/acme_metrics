package com.acme.metrics;

import com.telamin.fluxtion.runtime.annotations.OnTrigger;

public class AlertPublisher {

    private final ThresholdAlerter alerter;

    public AlertPublisher() {
        this(new ThresholdAlerter());
    }

    public AlertPublisher(ThresholdAlerter alerter) {
        this.alerter = alerter;
    }

    @OnTrigger
    public boolean onAlert() {
        System.out.println("[acme-metrics] ALERT #" + alerter.getAlertCount()
                + " reason=" + alerter.getLastReason());
        return true;
    }

    public ThresholdAlerter getAlerter() { return alerter; }
}
