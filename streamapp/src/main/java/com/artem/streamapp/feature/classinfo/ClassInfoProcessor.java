package com.artem.streamapp.feature.classinfo;

import com.artem.server.Features;
import com.artem.streamapp.base.ProcessorState;
import com.artem.streamapp.ext.AgentFeatureProcessor;
import com.artem.streamapp.ext.CommandState;
import com.artem.streamapp.feature.threads.ThreadMetadataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 03/06/2017
 */
public class ClassInfoProcessor extends AgentFeatureProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ClassInfoProcessor.class);

    @ProcessorState
    private ThreadMetadataStore threadMetadataStore;

    @ProcessorState
    private ClassInfoDataStore classInfoStore;

    public ClassInfoProcessor() {
        super(Features.CLASS_INFO, 2);
    }

    @Override
    protected void processFeatureData(Map<String, ?> data) {
        logger.info("Received class info data: " + data);
        classInfoStore.add(new ClassInfoData((Map<String, Map<String,List<String>>>) data));
    }

    @Override
    protected void punctuateActiveAgent(long timestamp) {
        Map<String, Set<String>> classMethods = threadMetadataStore.getClassMethods();
        if (!classMethods.isEmpty()) {
            Map<String, Set<String>> unknownMethods = classInfoStore.findUnknownMethods(classMethods);
            if (!unknownMethods.isEmpty()) {
                CommandState cmd = getCommandState();
                if (cmd == null || !cmd.inProgress())
                    sendCommand("getDeclaredMethods", unknownMethods);
            }
        }
    }

    @Override
    public void close() {

    }
}
