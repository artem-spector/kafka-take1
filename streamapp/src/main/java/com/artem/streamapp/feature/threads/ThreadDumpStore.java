package com.artem.streamapp.feature.threads;

import com.artem.streamapp.base.TimeWindow;
import com.artem.streamapp.ext.AgentStateStore;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 03/06/2017
 */
public class ThreadDumpStore extends AgentStateStore<TimeWindow<List<ThreadDump>>> {

    public ThreadDumpStore() {
        super("ThreadDumpStore", 60 * 1000,
                new TypeReference<TimeWindow<List<ThreadDump>>>() {
                });
    }

    public void putThreadDumps(List<ThreadDump> dumps) {
        getWindow(agentJVM()).putValue(timestamp(), dumps);
    }
}
