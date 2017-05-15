package com.artem.process;

import com.artem.server.JacksonSerdes;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
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

        TopologyBuilder builder = new TopologyBuilder()
                .addSource("AgentInput", "process-in-topic")
                .addProcessor(AgentInputProcessor.PROCESSOR_ID, AgentInputProcessor::new, "AgentInput")
                .addSink("ProcessorOutput", "process-out-topic", "InputProcessor");

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
