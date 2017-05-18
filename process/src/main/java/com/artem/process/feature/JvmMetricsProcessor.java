package com.artem.process.feature;

import com.artem.server.Features;

import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem on 16/05/2017.
 */
public class JvmMetricsProcessor extends FeatureInputProcessor {

    private static final int TEN_SEC = 10 * 1000;

    public JvmMetricsProcessor() {
        super(Features.JVM_METRICS, TEN_SEC);
    }

    @Override
    protected void processFeatureData(Map<String, Object> featureData) {
        Number cpu = (Number) featureData.get("cpu");
        if (cpu != null) {
            JvmMetrics metrics = new JvmMetrics();
            metrics.cpu = cpu.floatValue();
            state.addValueToTimeline(featureId, metrics);
            // TODO: forward to DB
        }
    }

    @Override
    public void punctuate(long l) {
    }

    @Override
    public void close() {
    }
}
