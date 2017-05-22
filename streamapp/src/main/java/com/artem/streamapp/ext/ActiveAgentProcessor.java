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
@ProcessorTopology(readFromTopics = {"AgentInput"})
public class ActiveAgentProcessor extends StatefulProcessor<AgentJVM, Object> {

    @ProcessorState
    private ActiveAgentsState state;

    @Override
    public void process(AgentJVM key, Object value) {
        state.registerActiveAgent(key, context.timestamp());
    }

    @Override
    public void punctuate(long timestamp) {
    }

    @Override
    public void close() {
    }
}
