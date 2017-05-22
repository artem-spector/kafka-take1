package com.artem.streamapp.base;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.lang.reflect.Field;
import java.util.*;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/21/17
 */
public abstract class StatefulProcessor<K, V> implements Processor<K, V> {

    protected ProcessorContext context;

    /**
     * Get the values of the fields annotated as ProcessorState, instantiate if null.
     * Fields of the same class will be populated with the same value
     *
     * @return collection of state values
     */
    public Collection<TimeWindowState> getStateFields() {
        Map<Class, TimeWindowState> res = new HashMap<>();
        for (Field field : getClass().getFields()) {
            if (field.getAnnotation(ProcessorState.class) != null) {
                if (!TimeWindowState.class.isAssignableFrom(field.getType()))
                    throw new RuntimeException("Field '" + field.getName() + "' is annotated with @ProcessorState, but its type is not " + TimeWindowState.class.getName());
                try {
                    Object value = field.get(this);
                    if (value == null) {
                        value = res.get(field.getType());
                        if (value == null) value = field.getType().newInstance();
                        field.set(this, value);
                    }
                    res.put(field.getType(), (TimeWindowState)value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot access value of field " + field.getName(), e);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Failed to instantiate field " + field.getName(), e);
                }
            }
        }
        return res.values();
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        for (TimeWindowState state : getStateFields()) state.init(context);
    }
}
