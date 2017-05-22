package com.artem.streamapp.ext;

import com.artem.server.AgentJVM;
import com.artem.streamapp.base.ProcessorState;
import com.artem.streamapp.base.ProcessorTopology;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains the command state and allows sending commands to the agent
 *
 * @author artem on 22/05/2017.
 */
@ProcessorTopology(parentSources = {"AgentInput"}, childSinks = {"OutgoingCommands"})
public abstract class AgentFeatureProcessor extends AgentProcessor<Map<String, Map<String, Object>>> {

    public final String featureId;

    @ProcessorState
    private CommandStateStore commands;

    protected AgentFeatureProcessor(String featureId, int punctuationIntervalSec) {
        super(featureId + "-processor", punctuationIntervalSec);
        this.featureId = featureId;
    }

    @Override
    public void process(AgentJVM agentJVM, Map<String, Map<String, Object>> features) {
        super.process(agentJVM, features);
        if (features.containsKey(featureId)) {
            Map<String, Object> featureData = features.get(featureId);
            parseCommand(featureData);
            processFeatureData(featureData);
        }
    }

    protected CommandState getCommandState() {
        return commands.getCommandState(featureId);
    }

    protected void sendCommand(String command, Map<String, Object> param) {
        CommandState cmd = new CommandState();
        cmd.command = command;
        cmd.sentAt = System.currentTimeMillis();
        commands.setCommandState(featureId, cmd);

        Map<String, Object> featureCmd = new HashMap<>();
        featureCmd.put("command", command);
        if (param != null) featureCmd.put("param", param);
        Map<String, Object> agentCmd = new HashMap<>();
        agentCmd.put(featureId, featureCmd);
        context.forward(agentJVM, agentCmd, "OutgoingCommands");
    }

    private void parseCommand(Map<String, Object> featureData) {
        Integer progress = (Integer) featureData.remove("progress");
        String error = (String) featureData.remove("error");
        if (progress != null || error != null) {
            CommandState cmd = getCommandState();
            if (cmd == null) cmd = new CommandState();
            if (progress != null) cmd.progress = progress;
            if (error != null) cmd.error = error;
            cmd.respondedAt = context.timestamp();
            commands.setCommandState(featureId, cmd);
        }
    }

    protected abstract void processFeatureData(Map<String, Object> data);
}
