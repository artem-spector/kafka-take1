package com.artem.producer.features;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/7/17
 */
public class JvmFeature extends FeatureDataProducer {

    public static final String FEATURE_ID = "JvmLoad";

    public JvmFeature() {
        super(FEATURE_ID);
    }

    @Override
    protected void processCommand() {
        Map<String, Object> json = new HashMap<>();
        json.put("cpu", randomFloat(0.8f, 12.3f));
        setData(50, json);
    }
}
