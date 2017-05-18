package com.artem.process.feature;

import com.artem.server.Features;

import java.util.List;
import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem on 18/05/2017.
 */
public class LiveThreadsProcessor extends FeatureInputProcessor {

    private static final int TIME_WINDOW_MS = 15 * 1000;

    public LiveThreadsProcessor() {
        super(Features.LIVE_THREADS, TIME_WINDOW_MS);
    }

    @Override
    protected void processFeatureData(Map<String, Object> featureData) {
        LiveThreads liveThreads = new LiveThreads();
        for (Map.Entry<String, Object> entry : featureData.entrySet()) {
            List threads = (List) entry.getValue();
            for (Object thread : threads) {
                liveThreads.addThread(entry.getKey(), (String) ((Map)thread).get("name"));
            }
        }

        state.addValueToTimeline(featureId, liveThreads);
    }

    @Override
    public void punctuate(long l) {

    }

    @Override
    public void close() {

    }
}
