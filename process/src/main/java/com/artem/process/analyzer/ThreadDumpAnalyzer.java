package com.artem.process.analyzer;

import com.artem.process.feature.AgentFeatureState;
import com.artem.process.feature.CommandState;
import com.artem.process.feature.LiveThreadsProcessor;
import com.artem.server.AgentJVM;
import com.artem.server.Features;

import java.util.Map;
import java.util.NavigableMap;

/**
 * TODO: Document!
 *
 * @author artem on 18/05/2017.
 */
public class ThreadDumpAnalyzer extends Analyzer {

    public static final long INTERVAL = 5 * 1000;

    @Override
    protected void processHeartbeat() {
        AgentFeatureState threadsState = getFeatureState(LiveThreadsProcessor.class);
        long now = System.currentTimeMillis();
        NavigableMap<Long, Map<AgentJVM, Object>> timeline = threadsState.getTimeline(Features.LIVE_THREADS, now - INTERVAL, now);
        if (timeline.isEmpty()) {
            CommandState commandState = threadsState.getCommandState();
            if (commandState == null || !commandState.inProgress())
                threadsState.sendCommand("dump", null);
        }
    }
}
