package com.artem.producer;

import com.artem.producer.features.ClassInfoProducer;
import com.artem.producer.features.FeatureDataProducer;
import com.artem.producer.features.LiveThreadsProducer;
import com.artem.producer.features.LoadDataProducer;
import com.artem.server.AgentJVM;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/6/17
 */
public class AgentMock {

    private AgentJVM key;
    private FeatureDataProducer[] features;

    public AgentMock(AgentJVM key) {
        this.key = key;
        features = new FeatureDataProducer[]{new LoadDataProducer(), new LiveThreadsProducer(), new ClassInfoProducer()};
    }

    public Map<String, Object> getData() throws JsonProcessingException {
        Map<String, Object> json = new HashMap<>();
        for (FeatureDataProducer feature : features) {
            Map<String, Object> featureData = feature.getFeatureData();
            if (featureData != null) {
                json.put(feature.featureId, featureData);
            }
        }

        return json;
    }

    public AgentJVM getKey() {
        return key;
    }

    public void setCommands(Map<String, Map<String, Object>> json) {
        if (json == null) return;

        for (FeatureDataProducer feature : features) {
            Map<String, Object> featureCommand = json.get(feature.featureId);
            if (featureCommand != null)
                feature.setCommand((String) featureCommand.get("command"), (Map<String, Object>) featureCommand.get("param"));
        }
    }

}
