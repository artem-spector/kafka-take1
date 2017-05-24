package com.artem.streamapp;

import com.artem.streamapp.base.StatefulProcessor;
import com.artem.streamapp.base.StreamsApplication;
import org.apache.kafka.streams.processor.ProcessorSupplier;

import java.util.*;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/24/17
 */
public class StreamsApplicationForTest extends StreamsApplication {

    private Map<String, List<ProcessorTestWrapper>> processors = new HashMap<>();

    public StreamsApplicationForTest(String appId, Properties topologyProperties, AutoOffsetReset autoOffsetReset) {
        super(appId, topologyProperties, autoOffsetReset);
    }

    @Override
    protected ProcessorSupplier getProcessorSupplier(Class<? extends StatefulProcessor> processorClass) {
        return () -> {
            try {
                StatefulProcessor processor = processorClass.newInstance();
                ProcessorTestWrapper testWrapper = new ProcessorTestWrapper(processor);
                processors.computeIfAbsent(processor.processorId, k -> new ArrayList<>()).add(testWrapper);
                return testWrapper;
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate processor", e);
            }
        };
    }
}
