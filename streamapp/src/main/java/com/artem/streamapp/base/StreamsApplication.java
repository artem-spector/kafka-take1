package com.artem.streamapp.base;

import com.artem.server.JacksonSerdes;
import javafx.util.Pair;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.StateStoreSupplier;
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
    private Properties topologyProperties;

    private Map<String, String[]> sources = new HashMap<>();
    private Map<String, Pair<String, List<String>>> sinks = new HashMap<>();
    private Map<Class<StatefulProcessor>, Pair<StatefulProcessor, List<String>>> processors = new HashMap<>();

    public StreamsApplication(String appId, Properties topologyProperties) {
        this.appId = appId;
        this.topologyProperties = topologyProperties;
    }

    public StreamsApplication addSource(String name, String... topics) {
        sources.put(name, topics);
        return this;
    }

    public StreamsApplication addSink(String name, String topic) {
        sinks.put(name, new Pair<>(topic, new ArrayList<>()));
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
            Pair<StatefulProcessor, List<String>> pair = resolve(processorClass, new ArrayList<>());
            builder.addProcessor(
                    pair.getKey().processorId,
                    () -> {
                        try {
                            return processorClass.newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to instantiate processor", e);
                        }
                    },
                    pair.getValue().toArray(new String[pair.getValue().size()]));
        }

        // sinks
        for (Map.Entry<String, Pair<String, List<String>>> entry : sinks.entrySet()) {
            Pair<String, List<String>> pair = entry.getValue();
            builder.addSink(entry.getKey(), pair.getKey(), pair.getValue().toArray(new String[pair.getValue().size()]));
        }

        // state stores
        Map<String, Pair<StateStoreSupplier, List<String>>> stores = new HashMap<>();
        for (Pair<StatefulProcessor, List<String>> pair : processors.values()) {
            StatefulProcessor processor = pair.getKey();
            for (TimeWindowStateStore store : (Collection<TimeWindowStateStore>) processor.getStateFields()) {
                stores.computeIfAbsent(store.storeId, key -> new Pair<>(store.createStoreSupplier(), new ArrayList<>()));
                stores.get(store.storeId).getValue().add(processor.processorId);
            }
        }
        for (Map.Entry<String, Pair<StateStoreSupplier, List<String>>> entry : stores.entrySet()) {
            Pair<StateStoreSupplier, List<String>> pair = entry.getValue();
            builder.addStateStore(pair.getKey(), pair.getValue().toArray(new String[pair.getValue().size()]));
        }

        // create app instance
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, topologyProperties.getProperty("bootstrap.servers"));
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, JacksonSerdes.AgentJVM().getClass());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, JacksonSerdes.Map().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return new KafkaStreams(builder, props);
    }

    private Pair<StatefulProcessor, List<String>> resolve(Class<StatefulProcessor> processorClass, List<Class<StatefulProcessor>> path) {
        if (!processors.containsKey(processorClass))
            throw new RuntimeException("Processor was not added to the application: " + processorClass.getName());

        Pair<StatefulProcessor, List<String>> pair = processors.get(processorClass);
        if (pair != null) return pair;

        // processor must be annotated
        ProcessorTopology annotation = processorClass.getAnnotation(ProcessorTopology.class);
        if (annotation == null)
            throw new RuntimeException("Processor class " + processorClass.getName() + " must be annotated with @" + ProcessorTopology.class.getName());

        // check circular dependency
        if (path.contains(processorClass)) {
            String pathStr = processorClass.getSimpleName() + "<-";
            for (int i = path.size() - 1; i <= 0; i--) {
                Class<StatefulProcessor> prev = path.get(i);
                pathStr += prev.getSimpleName();
                if (prev == processorClass) break;
                pathStr += "<-";
            }
            throw new RuntimeException("Circular dependency between the processors: " + pathStr);
        }

        // parent processors
        path.add(processorClass);
        List<String> parentIds = new ArrayList<>();
        for (Class<StatefulProcessor> parentClass : annotation.parentProcessors()) {
            Pair<StatefulProcessor, List<String>> parent = resolve(parentClass, path);
            parentIds.add(parent.getKey().processorId);
        }
        path.remove(processorClass);

        // parent sources
        for (String sourceId : annotation.parentSources()) {
            if (!sources.containsKey(sourceId))
                throw new RuntimeException("Source was not added to the application: " + sourceId);
            parentIds.add(sourceId);
        }

        // create instance
        StatefulProcessor instance;
        try {
            instance = processorClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create processor instance", e);
        }

        // child sinks
        for (String sinkId : annotation.childSinks()) {
            if (!sinks.containsKey(sinkId))
                throw new RuntimeException("Source was not added to the application: " + sinkId);
            sinks.get(sinkId).getValue().add(instance.processorId);
        }

        // create the pair and return
        pair = new Pair<>(instance, parentIds);
        processors.put(processorClass, pair);
        return pair;
    }
}
