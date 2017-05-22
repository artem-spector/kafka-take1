package com.artem.streamapp.ext;

import com.artem.streamapp.base.TimeWindow;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

/**
 * TODO: Document!
 *
 * @author artem on 22/05/2017.
 */
public class CommandStateStore extends AgentStateStore<TimeWindow<Map<String, CommandState>>> {

    private static long COMMAND_WINDOW_SIZE_MS = 30000;

    public CommandStateStore() {
        super("CommandStateStore", COMMAND_WINDOW_SIZE_MS, new TypeReference<TimeWindow<Map<String, CommandState>>>() {});
    }

    public CommandState getCommandState(String featureId) {
        NavigableMap<Long, Map<String, CommandState>> featureCommands = getWindow(agentJVM()).getValues(0, timestamp());
        for (Map<String, CommandState> map : featureCommands.descendingMap().values()) {
            CommandState featureCommand = map.get(featureId);
            if (featureCommand != null) return featureCommand;
        }
        return null;
    }

    public void setCommandState(String featureId, CommandState commandState) {
        updateWindow(window -> {
            long now = System.currentTimeMillis();
            Map<String, CommandState> featureCommands = window.getValue(now);
            if (featureCommands == null) {
                featureCommands = new HashMap<>();
                window.putValue(now, featureCommands);
            }
            featureCommands.put(featureId, commandState);
        });
    }

}
