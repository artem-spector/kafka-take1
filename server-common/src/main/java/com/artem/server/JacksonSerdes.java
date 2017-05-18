package com.artem.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/7/17
 */
public class JacksonSerdes extends Serdes {

    private static ObjectMapper mapper = new ObjectMapper();

    public static class JacksonSerializer<T> implements Serializer<T> {

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) { }

        @Override
        public byte[] serialize(String topic, Object data) {
            try {
                return mapper.writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                throw new SerializationException("Failed serializing object of class " + data.getClass().getName(), e);
            }
        }

        @Override
        public void close() { }
    }

    public static class JacksonDeserializer<T> implements Deserializer<T> {

        private Class<T> type;

        public JacksonDeserializer() {
        }

        public JacksonDeserializer(Class<T> type) {
            this.type = type;
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) { }

        @Override
        public T deserialize(String topic, byte[] data) {
            try {
                return mapper.readValue(data, type);
            } catch (IOException e) {
                throw new SerializationException("Failed deserializing object of type " + type.getName(), e);
            }
        }

        @Override
        public void close() { }
    }

    public static class AgentJVMDeserializer extends JacksonDeserializer<AgentJVM> {
        public AgentJVMDeserializer() {
            super(AgentJVM.class);
        }
    }

    public static class MapDeserializer extends JacksonDeserializer<Map> {
        public MapDeserializer() {
            super(Map.class);
        }
    }

    public static class AgentJVMSerde extends WrapperSerde<AgentJVM> {
        public AgentJVMSerde() {
            super(new JacksonSerializer<>(), new AgentJVMDeserializer());
        }
    }

    public static final class MapSerde extends WrapperSerde<Map> {
        public MapSerde() {
            super(new JacksonSerializer<>(), new MapDeserializer());
        }
    }

    public static Serde<AgentJVM> AgentJVM() {
        return new AgentJVMSerde();
    }

    public static Serde<Map> Map() {
        return new MapSerde();
    }
}
