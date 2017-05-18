package com.artem.process.feature;

import com.artem.server.JacksonSerdes;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.kafka.common.serialization.Serdes;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * TODO: Document!
 *
 * @author artem on 17/05/2017.
 */
public class TimeWindow<T> {

    public static class Serde extends Serdes {
        public static class TimeWindowSerde extends WrapperSerde<TimeWindow> {
            public TimeWindowSerde() {
                super(new JacksonSerdes.JacksonSerializer<>(), new JacksonSerdes.JacksonDeserializer<>(TimeWindow.class));
            }
        }
        public static Serde.TimeWindowSerde create() {
            return new TimeWindowSerde();
        }
    }

    private static final NavigableMap EMPTY = Collections.unmodifiableNavigableMap(new TreeMap(Collections.EMPTY_MAP));

    @JsonProperty
    private long maxSizeMillis;

    @JsonProperty
    private TreeMap<Long, T> window;

    public TimeWindow() {
    }

    public TimeWindow(Long maxSizeMillis) {
        this.maxSizeMillis = maxSizeMillis;
        window = new TreeMap<>();
    }

    public T getValue(long timestamp) {
        return window.get(timestamp);
    }

    public void putValue(long timestamp, T value) {
        window.put(timestamp, value);
        while (window.lastKey() - window.firstKey() > maxSizeMillis)
            window.remove(window.firstKey());
    }

    public NavigableMap<Long, T> getValues(long from, long to) {
        if (from > to)
            throw new IllegalArgumentException("from is greater than to");
        else if (window.isEmpty())
            return EMPTY;

        from = Math.max(from, window.firstKey());
        if (from > window.lastKey())
            return EMPTY;

        to = Math.min(to, window.lastKey());
        if (to < window.firstKey())
            return EMPTY;

        return Collections.unmodifiableNavigableMap(window.subMap(from, true, to, true));
    }

    @Override
    public String toString() {
        String str = "(";
        for (Map.Entry<Long, T> entry : window.entrySet()) {
            str += "\n\t" + entry.getKey();
            str += ": " + entry.getValue();
        }

        return str + ")";
    }
}
