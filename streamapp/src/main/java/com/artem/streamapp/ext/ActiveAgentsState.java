package com.artem.streamapp.ext;

import com.artem.server.AgentJVM;
import com.artem.streamapp.base.TimeWindow;
import com.artem.streamapp.base.TimeWindowState;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/20/17
 */
public class ActiveAgentsState extends TimeWindowState<String, TimeWindow<Set<AgentJVM>>> {

    private static final String SINGLE_ENTRY = "singleEntry";
    private static final long MAX_SIZE_MILLIS = 30 * 1000L;

    public ActiveAgentsState() {
        super("activeAgents", new TypeReference<String>() { }, new TypeReference<TimeWindow<Set<AgentJVM>>>() { });
    }

    public void registerActiveAgent(AgentJVM agentJVM, long time) {
        TimeWindow<Set<AgentJVM>> window = getTimeWindow();
        Set<AgentJVM> agents = window.getValue(time);
        if (agents == null)
            agents = new HashSet<>();
        agents.add(agentJVM);
        window.putValue(time, agents);
        put(SINGLE_ENTRY, window);
    }

    public Set<AgentJVM> getActiveAgents(long from, long to) {
        Set<AgentJVM> res = new HashSet<>();
        TimeWindow<Set<AgentJVM>> window = getTimeWindow();
        for (Set<AgentJVM> agents : window.getValues(from, to).values()) {
            res.addAll(agents);
        }
        return res;
    }

    private TimeWindow<Set<AgentJVM>> getTimeWindow() {
        TimeWindow<Set<AgentJVM>> window = get(SINGLE_ENTRY);
        return window == null ? new TimeWindow<>(MAX_SIZE_MILLIS) : window;
    }

}
