package com.artem.producer.features;

import com.artem.server.Features;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/7/17
 */
public class JvmMetricsProducer extends FeatureDataProducer {


    public JvmMetricsProducer() {
        super(Features.JVM_METRICS);
    }

    @Override
    protected void processCommand() {
        switch (command) {
            case "monitor":
                Map<String, Object> json = new HashMap<>();
                json.put("cpu", randomFloat(0.8f, 12.3f));
                setData(50, json);
                break;
            case "stop":
                setData(100, null);
                break;
            default:
                setError("Command '" + command + "' not supported by feature " + featureId);
                break;
        }
    }
}
