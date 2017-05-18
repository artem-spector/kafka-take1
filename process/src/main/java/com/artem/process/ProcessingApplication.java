package com.artem.process;

import com.artem.process.feature.JvmMetricsProcessor;
import com.artem.process.feature.LiveThreadsProcessor;
import com.artem.server.JacksonSerdes;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.TopologyBuilder;

import java.io.IOException;
import java.util.Properties;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/7/17
 */
public class ProcessingApplication {

    public static final String APP_ID = "agent-input-processing";
    private final KafkaStreams streams;

    public ProcessingApplication() throws IOException, InterruptedException {
        Properties topologyProp = new Properties();
        topologyProp.load(getClass().getClassLoader().getResourceAsStream("export.properties"));

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, topologyProp.getProperty("bootstrap.servers"));
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, JacksonSerdes.AgentJVM().getClass());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, JacksonSerdes.Map().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        JvmMetricsProcessor jvmMetricsProcessor = new JvmMetricsProcessor();
        LiveThreadsProcessor liveThreadsProcessor = new LiveThreadsProcessor();

        TopologyBuilder builder = new TopologyBuilder()
                .addSource("AgentInput", "process-in-topic")

                .addProcessor(AnalyzerProcessor.PROCESSOR_ID, AnalyzerProcessor::new, "AgentInput")
                .addProcessor(jvmMetricsProcessor.featureId, JvmMetricsProcessor::new, "AgentInput")
                .addProcessor(liveThreadsProcessor.featureId, LiveThreadsProcessor::new, "AgentInput")

                .addSink("OutgoingCommands", "command-topic", AnalyzerProcessor.PROCESSOR_ID)

                .addStateStore(new AnalyzerProcessor().createStoreSupplier(), AnalyzerProcessor.PROCESSOR_ID)
                .addStateStore(jvmMetricsProcessor.getState().createStoreSupplier(), jvmMetricsProcessor.featureId, AnalyzerProcessor.PROCESSOR_ID)
                .addStateStore(liveThreadsProcessor.getState().createStoreSupplier(), liveThreadsProcessor.featureId, AnalyzerProcessor.PROCESSOR_ID);

        streams = new KafkaStreams(builder, props);
    }

    private void start() {
        streams.start();
    }

    public static void main(String[] args) throws Exception {
        ProcessingApplication application = new ProcessingApplication();
        application.start();
    }
}
