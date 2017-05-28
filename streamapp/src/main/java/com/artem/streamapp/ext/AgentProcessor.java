package com.artem.streamapp.ext;

import com.artem.server.AgentJVM;
import com.artem.streamapp.base.ProcessorState;
import com.artem.streamapp.base.StatefulProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.Set;
import java.util.logging.Logger;

/**
 * TODO: Document!
 *
 * @author artem on 22/05/2017.
 */
public abstract class AgentProcessor<V> extends StatefulProcessor<AgentJVM, V> {

    private static final Logger logger = Logger.getLogger(AgentProcessor.class.getName());

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
        if (punctuationInterval > 0) {
            logger.info("scheduling " + getClass().getSimpleName() + " for " + punctuationInterval);
            context.schedule(punctuationInterval);
        }
    }

    @Override
    public void process(AgentJVM agentJVM, V v) {
        this.agentJVM = agentJVM;
    }

    @Override
    public void punctuate(long timestamp) {
        Set<AgentJVM> activeAgents = this.activeAgents.getActiveAgents(timestamp);
        logger.info("punctuate (" + timestamp + "); processor:" + getClass().getSimpleName() + "; active agents:" + activeAgents.size());
        for (AgentJVM agentJVM : activeAgents) {
            this.agentJVM = agentJVM;
            punctuateActiveAgent(timestamp);
        }
    }

    protected abstract void punctuateActiveAgent(long timestamp);
}
