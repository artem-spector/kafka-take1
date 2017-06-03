package com.artem.streamapp.feature.threads;

import com.artem.server.Features;
import com.artem.streamapp.ext.AgentFeatureProcessor;
import com.artem.streamapp.ext.CommandState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem on 01/06/2017.
 */
public class ThreadDumpProcessor extends AgentFeatureProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ThreadDumpProcessor.class);

    public ThreadDumpProcessor() {
        super(Features.LIVE_THREADS, 1);
    }

    @Override
    protected void processFeatureData(Map<String, Object> data) {
        logger.info("process thread dump: " + data);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey().startsWith("dump")) parseThreadDump((List<Map>)entry.getValue());
        }

    }

    @Override
    protected void punctuateActiveAgent(long timestamp) {
        CommandState commandState = getCommandState();
    }

    @Override
    public void close() {

    }

    private void parseThreadDump(List<Map> threads) {

    }
}
