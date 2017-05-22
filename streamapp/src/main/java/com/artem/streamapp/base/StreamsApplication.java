package com.artem.streamapp.base;

import com.artem.server.JacksonSerdes;
import javafx.util.Pair;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.TopologyBuilder;

import java.io.IOException;
import java.util.*;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/21/17
 */
public class StreamsApplication {

    public final String appId;

    private Map<String, String[]> sources = new HashMap<>();
    private Map<Class<StatefulProcessor>, Pair<StatefulProcessor, List<String>>> processors = new HashMap<>();

    public StreamsApplication(String appId) {
        this.appId = appId;
    }

    public StreamsApplication addSource(String name, String... topics) {
        sources.put(name, topics);
        return this;
    }

    public StreamsApplication addProcessors(Class<StatefulProcessor>... processors) {
        for (Class<StatefulProcessor> processor : processors) {
            this.processors.put(processor, null);
        }

        return this;
    }

    public KafkaStreams build() throws IOException {
        TopologyBuilder builder = new TopologyBuilder();

        // sources
        for (Map.Entry<String, String[]> entry : sources.entrySet()) {
            builder.addSource(entry.getKey(), entry.getValue());
        }

        // processors
        for (Class<StatefulProcessor> processorClass : processors.keySet()) {
            resolve(processorClass, new ArrayList<>());
        }

        // sinks

        // properties
        Properties topologyProp = new Properties();
        topologyProp.load(getClass().getClassLoader().getResourceAsStream("export.properties"));
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, topologyProp.getProperty("bootstrap.servers"));
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, JacksonSerdes.AgentJVM().getClass());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, JacksonSerdes.Map().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return new KafkaStreams(builder, props);
    }

    private void resolve(Class<StatefulProcessor> processorClass, List<Class<StatefulProcessor>> path) {
        if (processors.get(processorClass) != null) return;

        ProcessorTopology annotation = processorClass.getAnnotation(ProcessorTopology.class);
        if (annotation == null)
            throw new RuntimeException("Processor class " + processorClass.getName() + " must be annotated with @" + ProcessorTopology.class.getName());
    }
}
