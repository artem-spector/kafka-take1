package com.artem.producer.features;

import com.artem.server.Features;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/25/17
 */
public class LoadDataProducer extends FeatureDataProducer {

    static final String PROCESS_CPU_LOAD = "processCpuLoad";
    static final String HEAP_MEMORY_USAGE = "heapUsage";

    private final com.sun.management.OperatingSystemMXBean systemMXBean;
    private final MemoryMXBean memoryMXBean;

    public LoadDataProducer() {
        super(Features.JVM_METRICS);
        systemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    @Override
    protected void processCommand() {
        switch (command) {
            case "monitor":
                Map<String, Object> json = new HashMap<>();

                //cpu
                json.put(PROCESS_CPU_LOAD, systemMXBean.getProcessCpuLoad());
                // heap
                MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
                Map<String, Object> heapJson = new HashMap<>();
                json.put(HEAP_MEMORY_USAGE, heapJson);
                heapJson.put("used", heap.getUsed());
                heapJson.put("committed", heap.getCommitted());
                heapJson.put("max", heap.getMax());

                setData(50, json);
                break;
            case "stop":
                setData(100, null);
                break;
            default:
                invalidCommandError(command);
                break;
        }
    }
}
