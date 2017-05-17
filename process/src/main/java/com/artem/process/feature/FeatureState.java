package com.artem.process.feature;

import com.artem.server.AgentJVM;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

/**
 * TODO: Document!
 *
 * @author artem on 17/05/2017.
 */
public class FeatureState {

    protected String featureId;
    private long timeWindowSizeMs;

    protected ProcessorContext context;
    protected KeyValueStore<String, TimeWindow<Map<AgentJVM, Object>>> store;

    public FeatureState(String featureId, long timeWindowSizeMs) {
        this.featureId = featureId;
        this.timeWindowSizeMs = timeWindowSizeMs;
    }

    public StateStoreSupplier createStoreSupplier() {
        return Stores.create(featureId)
                .withKeys(Serdes.String())
                .withValues(TimeWindow.Serde.create())
                .inMemory()
                .build();
    }

    public void init(ProcessorContext context) {
        this.context = context;
        store = (KeyValueStore<String, TimeWindow<Map<AgentJVM, Object>>>) context.getStateStore(featureId);
    }

    public void addValueToTimeline(String key, AgentJVM agentJVM, Object value) {
        TimeWindow<Map<AgentJVM, Object>> window = getWindow(key);
        long now = context.timestamp();
        Map<AgentJVM, Object> agentValues = window.getValue(now);
        if (agentValues == null)
            agentValues = new HashMap<>();
        agentValues.put(agentJVM, value);
        window.putValue(now, agentValues);
        store.put(key, window);
    }

    public NavigableMap<Long, Map<AgentJVM, Object>> getTimeline(String key, long from, long to) {
        return getWindow(key).getValues(from, to);
    }

    protected TimeWindow<Map<AgentJVM, Object>> getWindow(String key) {
        TimeWindow<Map<AgentJVM, Object>> window = store.get(key);
        return window == null ? new TimeWindow<>(timeWindowSizeMs) : window;
    }

}
