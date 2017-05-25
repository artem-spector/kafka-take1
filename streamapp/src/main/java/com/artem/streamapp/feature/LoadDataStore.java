package com.artem.streamapp.feature;

import com.artem.streamapp.base.TimeWindow;
import com.artem.streamapp.ext.AgentStateStore;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/25/17
 */
public class LoadDataStore extends AgentStateStore<TimeWindow<LoadData>> {

    public LoadDataStore() {
        super("LoadDataStore", 60 * 1000, new TypeReference<TimeWindow<LoadData>>() { });
    }

    public void add(LoadData loadData) {
        updateWindow(window -> window.putValue(timestamp(), loadData));
    }
}
