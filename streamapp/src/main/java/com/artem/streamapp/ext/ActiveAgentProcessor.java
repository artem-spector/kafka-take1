package com.artem.streamapp.ext;

import com.artem.server.AgentJVM;
import com.artem.streamapp.base.ProcessorState;
import com.artem.streamapp.base.StatefulProcessor;
import com.artem.streamapp.base.ProcessorTopology;

import static com.artem.streamapp.ext.AgentFeatureProcessor.INPUT_SOURCE_ID;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/21/17
 */
@ProcessorTopology(parentSources = {INPUT_SOURCE_ID})
public class ActiveAgentProcessor extends StatefulProcessor<AgentJVM, Object> {

    @ProcessorState
    private ActiveAgentsStateStore agents;

    public ActiveAgentProcessor() {
        super("ActiveAgentProcessor");
    }

    @Override
    public void process(AgentJVM agentJVM, Object data) {
        super.process(agentJVM, data);
        agents.registerActiveAgent(agentJVM);
    }

    @Override
    public void punctuate(long timestamp) {
    }

    @Override
    public void close() {
    }
}
