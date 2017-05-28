package com.artem.streamapp.feature;

import com.artem.server.Features;
import com.artem.streamapp.base.ProcessorState;
import com.artem.streamapp.ext.AgentFeatureProcessor;
import com.artem.streamapp.ext.CommandState;

import java.util.Map;
import java.util.logging.Logger;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/25/17
 */
public class LoadDataProcessor extends AgentFeatureProcessor {

    private static final Logger logger = Logger.getLogger(LoadDataProcessor.class.getName());

    @ProcessorState
    private LoadDataStore loadDataStore;

    public LoadDataProcessor() {
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
        CommandState cmd = getCommandState();
        logger.info("punctuateActiveAgent(" + timestamp + "); command:" + cmd);
        if (cmd == null || !cmd.inProgress())
            sendCommand("monitor", null);
    }

    @Override
    public void close() {

    }
}
