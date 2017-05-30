package com.artem.producer.features;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/7/17
 */
public abstract class FeatureDataProducer {

    public final String featureId;
    protected String command;
    protected Map<String, Object> param;

    private String error;
    private Map<String, Object> data;
    private int progress;

    protected FeatureDataProducer(String featureId) {
        this.featureId = featureId;
    }

    public void setCommand(String command, Map<String, Object> param) {
        this.command = command;
        this.param = param;
        error = null;
        progress = 0;
    }

    protected void setError(String text) {
        error = text;
        data = null;
    }

    protected void invalidCommandError(String command) {
        setError("Command '" + command + "' not supported by feature " + featureId);
    }

    protected void setData(int progress, Map<String, Object> data) {
        this.progress = progress;
        this.data = data;
        error = null;
    }

    public Map<String, Object> getFeatureData() {
        if (command == null) return null;

        processCommand();
        if (data == null && error == null) return null;

        Map<String, Object> json = new HashMap<>();
        if (error == null) {
            json.put("progress", progress);
            json.putAll(data);
            if (progress == 100)
                clear();
        } else {
            json.put("error", error);
            clear();
        }
        return json;
    }

    private void clear() {
        command = null;
        param = null;
        error = null;
        data = null;
        progress = 0;
    }

    protected abstract void processCommand();
}
