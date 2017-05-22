package com.artem.streamapp;

import com.artem.server.AgentJVM;
import com.artem.server.JacksonSerdes;
import com.artem.streamapp.ext.ActiveAgentsState;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

import java.util.Set;

/**
 * Responsible for processing input data (type I) and maintaining a state (type S) for a specific feature.
 * Keeps the data belonging to different agents isolated from each other.
 *
 * @author artem
 *         Date: 5/20/17
 */
public abstract class FeatureDataProcessor<I, S> implements Processor<AgentJVM, I> {

    public final String featureId;
    private long windowSizeMs;
    private TypeReference<S> stateType;

    protected KeyValueStore<AgentJVM, S> stateStore;
    protected ActiveAgentsState activeAgents;

    protected ProcessorContext context;

    protected FeatureDataProcessor(String featureId, long windowSizeMs, TypeReference<S> stateType) {
        this.featureId = featureId;
        this.windowSizeMs = windowSizeMs;
        this.stateType = stateType;
        activeAgents = new ActiveAgentsState();
    }

    public StateStoreSupplier createStoreSupplier() {
        return Stores.create(featureId)
                .withKeys(JacksonSerdes.AgentJVM())
                .withValues(JacksonSerdes.jacksonSerde(stateType))
                .inMemory()
                .build();
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        activeAgents.init(context);
        stateStore = (KeyValueStore<AgentJVM, S>) context.getStateStore(featureId);
    }

    @Override
    public void punctuate(long timestamp) {
        Set<AgentJVM> activeAgents = this.activeAgents.getActiveAgents(timestamp - windowSizeMs, timestamp);
    }

    @Override
    public void close() {

    }
}
