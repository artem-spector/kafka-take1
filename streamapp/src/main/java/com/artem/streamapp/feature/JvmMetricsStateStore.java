package com.artem.streamapp.feature;

import com.artem.streamapp.base.TimeWindow;
import com.artem.streamapp.ext.AgentStateStore;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * TODO: Document!
 *
 * @author artem on 22/05/2017.
 */
public class JvmMetricsStateStore extends AgentStateStore<TimeWindow<JvmMetrics>> {

    private static long COMMAND_WINDOW_SIZE_MS = 30000;

    public JvmMetricsStateStore() {
        super("JvmMetricsStateStore",
                COMMAND_WINDOW_SIZE_MS,
                new TypeReference<TimeWindow<JvmMetrics>>() {
                });
    }

    public void addMetrics(JvmMetrics metrics) {
        updateWindow(window -> window.putValue(timestamp(), metrics));
    }
}
