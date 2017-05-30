package com.artem.streamapp.feature.load;

import com.artem.streamapp.base.SlidingWindow;
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

    public void add(RawLoadData rawData) {
        LoadData loadData = new LoadData();
        loadData.rawData = rawData;
        updateWindow(window -> window.putValue(timestamp(), loadData));
    }

    public void processSlidingData(int maxPrevValues, int maxNextValues, SlidingWindow.Visitor<LoadData> visitor) {
        updateWindow(window -> {
            new SlidingWindow<>(window.getRecentValues()).scanValues(maxPrevValues, maxNextValues, visitor);
        });
    }

}
