package com.artem.streamapp.base;

import javax.annotation.Resource;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates fields of type {@link TimeWindowState}, that will be automatically created and initialized
 *
 * @author artem
 *         Date: 5/21/17
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessorState {
}
