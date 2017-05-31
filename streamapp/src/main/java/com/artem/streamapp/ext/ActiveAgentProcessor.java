package com.artem.streamapp.ext;

import com.artem.server.AgentJVM;
import com.artem.streamapp.base.ProcessorState;
import com.artem.streamapp.base.StatefulProcessor;
import com.artem.streamapp.base.ProcessorTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.artem.streamapp.ext.AgentFeatureProcessor.INPUT_SOURCE_ID;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/21/17
 */
@ProcessorTopology(parentSources = {INPUT_SOURCE_ID})
public class ActiveAgentProcessor extends StatefulProcessor<AgentJVM, Object> {

    private static final Logger logger = LoggerFactory.getLogger(ActiveAgentProcessor.class);

    @ProcessorState
    private ActiveAgentsStateStore agents;

    public ActiveAgentProcessor() {
        super("ActiveAgentProcessor");
    }

    @Override
    public void process(AgentJVM agentJVM, Object data) {
        logger.debug("Registering active agent " + agentJVM + " app:" + context.applicationId() + "; task:" + context.taskId() + "; ctx.timestamp:" + context.timestamp() + "; now:" + System.currentTimeMillis() + ": data: " + data);
        agents.registerActiveAgent(agentJVM);
        context.commit();
    }

    @Override
    public void punctuate(long timestamp) {
    }

    @Override
    public void close() {
    }
}
