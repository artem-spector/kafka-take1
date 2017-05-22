package com.artem.streamapp.base;

import com.artem.server.AgentJVM;
import org.apache.kafka.streams.state.KeyValueStore;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/20/17
 */
public class FeatureStateStore<S> {

    private KeyValueStore<AgentJVM, S> store;

}
