package com.artem.streamapp.ext;

import com.artem.server.AgentJVM;
import com.artem.streamapp.base.TimeWindow;
import com.artem.streamapp.base.TimeWindowStateStore;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashSet;
import java.util.Set;

/**
 * Keeps agentJVMs active during the window
 *
 * @author artem
 *         Date: 5/20/17
 */
public class ActiveAgentsStateStore extends TimeWindowStateStore<String, TimeWindow<Set<AgentJVM>>> {

    private static final String SINGLE_ENTRY = "singleEntry";
    private static final long MAX_SIZE_MILLIS = 30 * 1000L;

    public ActiveAgentsStateStore() {
        super("activeAgents", MAX_SIZE_MILLIS, new TypeReference<String>() {
        }, new TypeReference<TimeWindow<Set<AgentJVM>>>() {
        });
    }

    public void registerActiveAgent(AgentJVM agentJVM) {
        TimeWindow<Set<AgentJVM>> window = getWindow(SINGLE_ENTRY);
        long now = timestamp();
        Set<AgentJVM> agents = window.getValue(now);
        if (agents == null)
            agents = new HashSet<>();
        agents.add(agentJVM);
        window.putValue(now, agents);
        putWindow(SINGLE_ENTRY, window);
    }

    /**
     * Get the agents that were active from the beginning of the window to the given time
     *
     * @param to the time for which the query is made
     * @return agents that were active from the beginning of the window
     */
    public Set<AgentJVM> getActiveAgents(long to) {
        Set<AgentJVM> res = new HashSet<>();
        TimeWindow<Set<AgentJVM>> window = getWindow(SINGLE_ENTRY);
        for (Set<AgentJVM> agents : window.getValues(to - MAX_SIZE_MILLIS, to).values()) {
            res.addAll(agents);
        }
        return res;
    }

}
