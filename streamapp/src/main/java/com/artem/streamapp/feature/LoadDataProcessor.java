package com.artem.streamapp.feature;

import com.artem.server.Features;
import com.artem.streamapp.base.ProcessorState;
import com.artem.streamapp.ext.AgentFeatureProcessor;

import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/25/17
 */
public class LoadDataProcessor extends AgentFeatureProcessor {

    @ProcessorState
    private LoadDataStore loadDataStore;

    protected LoadDataProcessor() {
        super(Features.JVM_METRICS, 1);
    }

    @Override
    protected void processFeatureData(Map<String, Object> json) {
        LoadData data = new LoadData();
        data.processCpuLoad = ((Number)json.get("processCpuLoad")).floatValue();
        Map<String, Object> heapJson = (Map<String, Object>) json.get("heapUsage");
        data.heapCommitted = ((Number)heapJson.get("heapCommitted")).floatValue();
        data.heapUsed = ((Number)heapJson.get("heapUsed")).floatValue();
        data.heapMax = ((Number)heapJson.get("heapMax")).floatValue();

        loadDataStore.add(data);
    }

    @Override
    protected void punctuateActiveAgent(long timestamp) {

    }

    @Override
    public void close() {

    }
}
