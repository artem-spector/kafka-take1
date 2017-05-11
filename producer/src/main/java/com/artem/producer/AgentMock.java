package com.artem.producer;

import com.artem.producer.features.FeatureDataProducer;
import com.artem.producer.features.JvmFeature;
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
        features = new FeatureDataProducer[]{new JvmFeature()};
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

    public void setCommand(Map<String, Object> json) {
        if (json == null) return;

        for (FeatureDataProducer feature : features) {
            Map<String, Object> featureCommand = (Map<String, Object>) json.get(feature.featureId);
            if (featureCommand != null)
                feature.setCommand((String) featureCommand.get("command"), (Map<String, Object>) featureCommand.get("param"));
        }
    }

    public void startMonitoring() {
        Map<String, Object> json = new HashMap<>();
        Map<String, Object> command = new HashMap<>();
        command.put("command", "start");
        json.put(JvmFeature.FEATURE_ID, command);
        setCommand(json);
    }
}
