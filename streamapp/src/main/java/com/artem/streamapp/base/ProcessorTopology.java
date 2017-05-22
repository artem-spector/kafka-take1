package com.artem.streamapp.base;

import org.apache.kafka.streams.processor.Processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/21/17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessorTopology {
    String[] readFromTopics() default  {};
    String[] writeToTopics() default  {};
    Class<Processor>[] readFromProcessors() default {};
    Class<Processor>[] writeToProcessors() default {};
}
