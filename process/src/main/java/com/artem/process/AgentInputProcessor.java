package com.artem.process;

import com.artem.server.AgentJVM;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStore;

import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/7/17
 */
public class AgentInputProcessor implements Processor<AgentJVM, Map<String, Object>> {

    public static final String PROCESSOR_ID = "InputProcessor";

    private ProcessorContext context;

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public void process(AgentJVM key, Map<String, Object> value) {
        StateStore store = context.getStateStore("aaa");
        context.forward(key, value);
    }

    @Override
    public void punctuate(long timestamp) { }

    @Override
    public void close() { }
}
