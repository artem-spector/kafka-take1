package com.artem.process.feature;

import com.artem.server.AgentJVM;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

/**
 * TODO: Document!
 *
 * @author artem on 15/05/2017.
 */
public class AgentFeatureState extends FeatureState {


    private static final String COMMAND_STATE_KEY = "command";
    private AgentJVM agentJVM;

    public AgentFeatureState(String featureId, long widowSizeMs) {
        super(featureId, widowSizeMs);
    }

    public void init(ProcessorContext context, AgentJVM agentJVM) {
        super.init(context);
        this.agentJVM = agentJVM;
    }

    public CommandState getCommandState() {
        CommandState res = null;

        NavigableMap<Long, Map<AgentJVM, Object>> timeline = getTimeline(COMMAND_STATE_KEY, 0, context.timestamp());
        for (Map<AgentJVM, Object> agentValues : timeline.descendingMap().values()) {
            if (agentValues.containsKey(agentJVM)) {
                res = (CommandState) agentValues.get(agentJVM);
                break;
            }
        }

        return res;
    }

    public void updateCommandState(Map<String, Object> featureData) {
        Integer progress = (Integer) featureData.remove("progress");
        String error = (String) featureData.remove("error");
        if (progress != null || error != null) {
            CommandState cmd = getCommandState();
            if (cmd == null) cmd = new CommandState();
            if (progress != null) cmd.progress = progress;
            if (error != null) cmd.error = error;
            cmd.respondedAt = context.timestamp();
            addValueToTimeline(COMMAND_STATE_KEY, cmd);
        }
    }

    public void sendCommand(String command, Map<String, Object> param) {
        CommandState cmd = new CommandState();
        cmd.command = command;
        cmd.sentAt = context.timestamp();
        addValueToTimeline(COMMAND_STATE_KEY, cmd);

        Map<String, Object> featureCmd = new HashMap<>();
        featureCmd.put(COMMAND_STATE_KEY, command);
        if (param != null) featureCmd.put("param", param);
        Map<String, Object> agentCmd = new HashMap<>();
        agentCmd.put(featureId, featureCmd);
        context.forward(agentJVM, agentCmd, "OutgoingCommands");
    }

    public void addValueToTimeline(String key, Object value) {
        super.addValueToTimeline(key, agentJVM, value);
    }
}
