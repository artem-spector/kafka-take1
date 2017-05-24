package com.artem.streamapp.base;

import org.apache.kafka.streams.processor.ProcessorSupplier;

import java.util.*;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/24/17
 */
public class StreamsApplicationForTest extends StreamsApplication {

    private boolean clearStates;
    private Map<Class<? extends StatefulProcessor>, List<ProcessorTestWrapper>> processors = new HashMap<>();

    public StreamsApplicationForTest(String appId, Properties topologyProperties, AutoOffsetReset autoOffsetReset, boolean clearStates) {
        super(appId, topologyProperties, autoOffsetReset);
        this.clearStates = clearStates;
    }

    @Override
    protected ProcessorSupplier getProcessorSupplier(Class<? extends StatefulProcessor> processorClass) {
        return () -> {
            try {
                StatefulProcessor processor = processorClass.newInstance();
                if (clearStates) processor.clearState();
                ProcessorTestWrapper testWrapper = new ProcessorTestWrapper(processor);
                processors.computeIfAbsent(processorClass, k -> new ArrayList<>()).add(testWrapper);
                return testWrapper;
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate processor", e);
            }
        };
    }

}
