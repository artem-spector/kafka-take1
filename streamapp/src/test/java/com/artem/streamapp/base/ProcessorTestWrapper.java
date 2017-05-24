package com.artem.streamapp.base;

import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.Collection;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/24/17
 */
public class ProcessorTestWrapper extends StatefulProcessor {

    private StatefulProcessor delegate;

    public ProcessorTestWrapper(StatefulProcessor delegate) {
        super(delegate.processorId);
        this.delegate = delegate;
    }

    @Override
    public Collection<TimeWindowStateStore> getStateFields() {
        return delegate.getStateFields();
    }

    @Override
    public void init(ProcessorContext context) {
        delegate.init(context);
    }

    @Override
    public void process(Object key, Object value) {
        delegate.process(key, value);
    }

    @Override
    public void punctuate(long timestamp) {
        delegate.punctuate(timestamp);
    }

    @Override
    public void close() {
        delegate.close();
    }

}
