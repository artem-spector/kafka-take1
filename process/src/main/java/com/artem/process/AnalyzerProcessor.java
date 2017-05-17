package com.artem.process;

import com.artem.process.analyzer.Analyzer;
import com.artem.process.analyzer.JvmLoadAnalyzer;
import com.artem.process.feature.FeatureState;
import com.artem.server.AgentJVM;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStoreSupplier;

import java.util.*;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/7/17
 */
public class AnalyzerProcessor implements Processor<AgentJVM, Map> {

    static final String PROCESSOR_ID = "AnalyzerProcessor";

    private static final String ACTIVE_JVMS_KEY = "activeJVMs";
    private static final long ACTIVE_AGENTS_WINDOW = 10 * 60 * 1000;
    private static final long AGENT_TIMEOUT_MS = 30 * 1000;
    private static final int PUNCTUATE_INTERVAL_MS = 1000;

    private ProcessorContext context;
    private List<Analyzer> analyzers = Arrays.asList(new Analyzer[] {new JvmLoadAnalyzer()});
    private FeatureState state = new FeatureState(PROCESSOR_ID, ACTIVE_AGENTS_WINDOW);

    public StateStoreSupplier createStoreSupplier() {
        return state.createStoreSupplier();
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        state.init(context);
        context.schedule(PUNCTUATE_INTERVAL_MS);
    }

    @Override
    public void process(AgentJVM agentJVM, Map value) {
        state.addValueToTimeline(ACTIVE_JVMS_KEY, agentJVM, null);
    }

    @Override
    public void punctuate(long timestamp) {
        Set<AgentJVM> aliveAgents = new HashSet<>();
        for (Map<AgentJVM, Object> agents : state.getTimeline(ACTIVE_JVMS_KEY, timestamp - AGENT_TIMEOUT_MS, timestamp).values()) {
            aliveAgents.addAll(agents.keySet());
        }

        for (AgentJVM agentJVM : aliveAgents) {
            for (Analyzer analyzer : analyzers) {
                analyzer.heartbeat(agentJVM, context);
            }
        }

    }


    @Override
    public void close() { }
}
