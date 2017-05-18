package com.artem.producer.features;

import com.artem.server.Features;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem on 18/05/2017.
 */
public class LiveThreadsProducer extends FeatureDataProducer {


    private ThreadMXBean threadMXBean;

    public LiveThreadsProducer() {
        super(Features.LIVE_THREADS);
        threadMXBean = ManagementFactory.getThreadMXBean();
    }

    @Override
    protected void processCommand() {
        if ("dump".equals(command)) {
            boolean lockedMonitors = false;
            boolean lockedSynchronizers = false;
            int num = 1;
            int interval = 0;

            if (param != null) {
                Number val = (Number) param.get("num");
                if (val != null) num = val.intValue();

                if (num > 1) {
                    val = (Number) param.get("interval");
                    interval = val.intValue();
                }
            }

            setData(100, dumpThreads(num, interval, lockedMonitors, lockedSynchronizers));
        } else {
            invalidCommandError(command);
        }
    }

    private Map<String, Object> dumpThreads(int num, int interval, boolean lockedMonitors, boolean lockedSynchronizers) {
        Map<String, Object> res = new HashMap<>();

        for (int i = 0; i < num; i++) {
            List threads = (List) res.computeIfAbsent("dump" + (i +1), k -> new ArrayList<Map<String, Object>>());
            for (ThreadInfo info : threadMXBean.dumpAllThreads(lockedMonitors, lockedSynchronizers)) {
                Map<String, Object> thread = new HashMap<>();
                thread.put("name", info.getThreadName());
                threads.add(thread);
            }


            if (i < num - 1) try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        return res;
    }
}
