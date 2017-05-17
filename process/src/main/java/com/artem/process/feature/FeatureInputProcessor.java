package com.artem.process.feature;

import com.artem.server.AgentJVM;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/13/17
 */
public abstract class FeatureInputProcessor implements Processor<AgentJVM, Map<String, Map<String, Object>>> {

    public final String featureId;
    private ProcessorContext context;
    protected AgentFeatureState state;

    public FeatureInputProcessor(String featureId, long timeWindowMs) {
        this.featureId = featureId;
        this.state = new AgentFeatureState(featureId, timeWindowMs);
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public void process(AgentJVM agentJVM, Map<String, Map<String, Object>> json) {
        Map<String, Object> featureData = json.get(featureId);
        if (featureData != null) {
            state.init(context, agentJVM);
            state.updateCommandState(featureData);
            processFeatureData(featureData);
        }
    }

    protected abstract void processFeatureData(Map<String, Object> featureData);

    public AgentFeatureState getState() {
        return state;
    }
}
