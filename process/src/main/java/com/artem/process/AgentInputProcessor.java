package com.artem.process;

import com.artem.server.AgentJVM;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/7/17
 */
public class AgentInputProcessor implements Processor<AgentJVM, Map<String, Object>> {

    private ProcessorContext context;

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public void process(AgentJVM key, Map<String, Object> value) {
        context.forward(key, value);
    }

    @Override
    public void punctuate(long timestamp) { }

    @Override
    public void close() { }
}
