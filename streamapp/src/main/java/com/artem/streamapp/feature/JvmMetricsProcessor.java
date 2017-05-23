package com.artem.streamapp.feature;

import com.artem.server.Features;
import com.artem.streamapp.base.ProcessorState;
import com.artem.streamapp.ext.AgentFeatureProcessor;
import com.artem.streamapp.ext.CommandState;

import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem on 22/05/2017.
 */
public class JvmMetricsProcessor extends AgentFeatureProcessor {

    @ProcessorState
    private JvmMetricsStateStore metricsStore;

    public JvmMetricsProcessor() {
        super(Features.JVM_METRICS, 1);
    }

    @Override
    protected void processFeatureData(Map<String, Object> data) {
        Number cpu = (Number) data.get("cpu");
        if (cpu != null) {
            JvmMetrics metrics = new JvmMetrics();
            metrics.cpu = cpu.floatValue();
            metricsStore.addMetrics(metrics);
            // TODO: forward to DB
        }
    }

    @Override
    protected void punctuateActiveAgent(long timestamp) {
        CommandState cmd = getCommandState();
        if (cmd == null || !cmd.inProgress())
            sendCommand("monitor", null);
    }

    @Override
    public void close() {

    }
}
