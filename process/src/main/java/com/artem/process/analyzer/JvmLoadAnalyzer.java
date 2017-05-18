package com.artem.process.analyzer;

import com.artem.process.feature.AgentFeatureState;
import com.artem.process.feature.CommandState;
import com.artem.process.feature.JvmMetricsProcessor;

/**
 * TODO: Document!
 *
 * @author artem on 17/05/2017.
 */
public class JvmLoadAnalyzer extends Analyzer {

    @Override
    protected void processHeartbeat() {
        AgentFeatureState metricState = getFeatureState(JvmMetricsProcessor.class);
        CommandState metricCommandState = metricState.getCommandState();
        if (metricCommandState == null || !metricCommandState.inProgress())
            metricState.sendCommand("monitor", null);
    }
}
