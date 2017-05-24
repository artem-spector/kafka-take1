package com.artem.streamapp.base;

import com.artem.server.JacksonSerdes;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

/**
 * Key/value state store, where the value is a time window
 *
 * @author artem
 *         Date: 5/21/17
 */
public abstract class TimeWindowStateStore<K, V extends TimeWindow> {

    public final String storeId;
    private long maxSizeMillis;

    private final TypeReference<K> keyType;
    private final TypeReference<V> windowType;
    private final Class<V> windowClass;

    protected StatefulProcessor container;
    private KeyValueStore<K, V> store;

    protected TimeWindowStateStore(String storeId, long maxSizeMillis, TypeReference<K> keyType, TypeReference<V> windowType) {
        this.storeId = storeId;
        this.maxSizeMillis = maxSizeMillis;
        this.keyType = keyType;
        this.windowType = windowType;
        try {
            String className = windowType.getType().getTypeName();
            className = className.substring(0, className.indexOf("<"));
            windowClass = (Class<V>) Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate store " + storeId, e);
        }
    }

    StateStoreSupplier createStoreSupplier() {
        return Stores.create(storeId)
                .withKeys(JacksonSerdes.jacksonSerde(keyType))
                .withValues(JacksonSerdes.jacksonSerde(windowType))
                .inMemory()
                .build();
    }

    void init(StatefulProcessor container) {
        this.container = container;
        store = (KeyValueStore<K, V>) container.context.getStateStore(storeId);
    }

    protected V getWindow(K key) {
        V window = store.get(key);
        if (window == null) {
            try {
                window = windowClass.newInstance();
                window.init(maxSizeMillis);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize store " + storeId, e);
            }
        }
        return window;
    }

    protected void putWindow(K key, V value) {
        store.put(key, value);
    }

    protected long timestamp() {
        return container.context.timestamp();
    }

}
