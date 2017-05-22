package com.artem.streamapp.base;

import com.artem.server.JacksonSerdes;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

/**
 * Key - value state store, where value is a time window
 *
 * @author artem
 *         Date: 5/21/17
 */
public abstract class TimeWindowState<K, V extends TimeWindow> {

    public final String storeId;

    private final TypeReference<K> keyType;
    private final TypeReference<V> valueType;
    private KeyValueStore<K, V> store;

    protected TimeWindowState(String storeId, TypeReference<K> keyType, TypeReference<V> valueType) {
        this.storeId = storeId;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public StateStoreSupplier createStoreSupplier() {
        return Stores.create(storeId)
                .withKeys(JacksonSerdes.jacksonSerde(keyType))
                .withValues(JacksonSerdes.jacksonSerde(valueType))
                .inMemory()
                .build();
    }

    public void init(ProcessorContext ctx) {
        store = (KeyValueStore<K, V>) ctx.getStateStore(storeId);
    }

    public V get(K key) {
        return store.get(key);
    }

    public void put(K key, V value) {
        store.put(key, value);
    }

}
