package com.artem.streamapp.ext;

import com.artem.server.AgentJVM;
import com.artem.streamapp.base.ProcessorState;
import com.artem.streamapp.base.StatefulProcessor;
import com.artem.streamapp.base.ProcessorTopology;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/21/17
 */
@ProcessorTopology(parentSources = {"AgentInput"})
public class ActiveAgentProcessor extends StatefulProcessor<AgentJVM, Object> {

    @ProcessorState
    private ActiveAgentsStateStore agents;

    public ActiveAgentProcessor() {
        super("ActiveAgentProcessor");
    }

    @Override
    public void process(AgentJVM agentJVM, Object data) {
        agents.registerActiveAgent(agentJVM);
    }

    @Override
    public void punctuate(long timestamp) {
    }

    @Override
    public void close() {
    }
}
