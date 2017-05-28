package com.artem.streamapp.ext;

import com.artem.server.AgentJVM;
import com.artem.streamapp.base.ProcessorState;
import com.artem.streamapp.base.ProcessorTopology;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.artem.streamapp.ext.AgentFeatureProcessor.*;

/**
 * Maintains the command state and allows sending commands to the agent
 *
 * @author artem on 22/05/2017.
 */
@ProcessorTopology(parentSources = {INPUT_SOURCE_ID}, childSinks = {COMMANDS_SINK_ID})
public abstract class AgentFeatureProcessor extends AgentProcessor<Map<String, Map<String, Object>>> {

    private static final Logger logger = Logger.getLogger(AgentFeatureProcessor.class.getName());

    public static final String INPUT_SOURCE_ID = "AgentInput";
    public static final String COMMANDS_SINK_ID = "OutgoingCommands";

    public final String featureId;

    @ProcessorState
    private CommandStateStore commands;

    protected AgentFeatureProcessor(String featureId, int punctuationIntervalSec) {
        super(featureId + "-processor", punctuationIntervalSec);
        this.featureId = featureId;
    }

    @Override
    public void process(AgentJVM agentJVM, Map<String, Map<String, Object>> features) {
//        logger.info("process " + agentJVM + " feature " + featureId);
        super.process(agentJVM, features);
        if (features.containsKey(featureId)) {
            Map<String, Object> featureData = features.get(featureId);
            parseCommand(featureData);
            processFeatureData(featureData);
        }
        context.commit();
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
        context.forward(agentJVM, agentCmd, COMMANDS_SINK_ID);
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
