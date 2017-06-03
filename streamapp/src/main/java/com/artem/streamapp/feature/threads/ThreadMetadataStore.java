package com.artem.streamapp.feature.threads;

import com.artem.streamapp.base.TimeWindow;
import com.artem.streamapp.ext.AgentStateStore;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Set;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 03/06/2017
 */
public class ThreadMetadataStore extends AgentStateStore<TimeWindow<Set<ThreadMetadata>>> {

    public ThreadMetadataStore() {
        super("ThreadMetadataStore", 60 * 60 * 1000,
                new TypeReference<TimeWindow<Set<ThreadMetadata>>>() {
                });
    }

    public void putMetadata(Set<ThreadMetadata> metadata) {
        getWindow(agentJVM()).putValue(timestamp(), metadata);
    }
}
