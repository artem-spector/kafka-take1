package com.artem.producer;

import com.artem.server.AgentJVM;
import com.artem.server.JacksonSerdes;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.*;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/14/17
 */
public class CommandsTopicConsumer {

    private final KafkaConsumer<AgentJVM, Map<String, Map<String, Object>>> consumer;
    private Map<AgentJVM, Map<String, Map<String, Object>>> agentFeatureCommands = new HashMap<>();

    public CommandsTopicConsumer(String topicName, String consumerGroup) throws IOException {
        Properties topologyProp = new Properties();
        topologyProp.load(getClass().getClassLoader().getResourceAsStream("export.properties"));

        Properties props = new Properties();
        props.put("bootstrap.servers", topologyProp.getProperty("bootstrap.servers"));
        props.put("group.id", consumerGroup);
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", JacksonSerdes.AgentJVM().deserializer().getClass().getName());
        props.put("value.deserializer", JacksonSerdes.Map().deserializer().getClass().getName());
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topicName));
    }

    public Map<String, Map<String, Object>> getFeatureCommands(AgentJVM key) {
        synchronized (consumer) {
            ConsumerRecords<AgentJVM, Map<String, Map<String, Object>>> records = consumer.poll(0);
            for (ConsumerRecord<AgentJVM, Map<String, Map<String, Object>>> record : records) {
                agentFeatureCommands.put(record.key(), record.value());
            }
            consumer.commitSync();
        }

        return agentFeatureCommands.remove(key);
    }
}
