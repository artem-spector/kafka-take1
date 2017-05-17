package com.artem.process.analyzer;

import com.artem.process.feature.AgentFeatureState;
import com.artem.process.feature.FeatureInputProcessor;
import com.artem.server.AgentJVM;
import org.apache.kafka.streams.processor.ProcessorContext;

/**
 * TODO: Document!
 *
 * @author artem on 17/05/2017.
 */
public abstract class Analyzer {

    private static ThreadLocal<AgentJVM> agentJVM = new ThreadLocal<>();
    private static ThreadLocal<ProcessorContext> context = new ThreadLocal<>();

    public void heartbeat(AgentJVM agentJVM, ProcessorContext context) {
        Analyzer.agentJVM.set(agentJVM);
        Analyzer.context.set(context);

        processHeartbeat();

        Analyzer.agentJVM.remove();
        Analyzer.context.remove();
    }

    protected AgentFeatureState getFeatureState(Class<? extends FeatureInputProcessor> processorClass) {
        try {
            AgentFeatureState state = processorClass.newInstance().getState();
            state.init(context.get(), agentJVM.get());
            return state;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void processHeartbeat();
}
