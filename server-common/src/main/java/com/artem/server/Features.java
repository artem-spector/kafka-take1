package com.artem.server;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/13/17
 */
public interface Features {

    public static final String JVM_METRICS = "jvm-metrics";
    public static final String LIVE_THREADS = "live-threads";
    public static final String INSTRUMENTATION_CONFIG = "instr-conf";

    public static final String[] ALL_FEATURES = {JVM_METRICS, LIVE_THREADS, INSTRUMENTATION_CONFIG};
}
