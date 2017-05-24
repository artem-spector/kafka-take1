package com.artem.streamapp.ext;

import com.artem.server.AgentJVM;
import com.artem.streamapp.base.ProcessorState;
import com.artem.streamapp.base.StatefulProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;

/**
 * TODO: Document!
 *
 * @author artem on 22/05/2017.
 */
public abstract class AgentProcessor<V> extends StatefulProcessor<AgentJVM, V> {

    protected AgentJVM agentJVM;
    private long punctuationInterval;

    @ProcessorState
    private ActiveAgentsStateStore activeAgents;

    protected AgentProcessor(String processorId, int punctuationIntervalSec) {
        super(processorId);
        if (punctuationIntervalSec >= 1) this.punctuationInterval = punctuationIntervalSec * 1000;
    }

    @Override
    public void init(ProcessorContext context) {
        super.init(context);
        agentJVM = null;
        if (punctuationInterval > 0)
            context.schedule(punctuationInterval);
    }

    @Override
    public void process(AgentJVM agentJVM, V v) {
        this.agentJVM = agentJVM;
    }

    @Override
    public void punctuate(long timestamp) {
        for (AgentJVM agentJVM : activeAgents.getActiveAgents(timestamp)) {
            this.agentJVM = agentJVM;
            punctuateActiveAgent(timestamp);
        }
    }

    protected abstract void punctuateActiveAgent(long timestamp);
}
