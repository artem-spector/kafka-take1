package com.artem.process.feature;

import com.artem.server.AgentJVM;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/13/17
 */
public abstract class FeatureInputProcessor<S> implements Processor<AgentJVM, S> {

    private ProcessorContext context;

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }
}
