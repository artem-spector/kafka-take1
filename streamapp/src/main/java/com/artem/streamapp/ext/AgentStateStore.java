package com.artem.streamapp.ext;

import com.artem.server.AgentJVM;
import com.artem.streamapp.base.TimeWindow;
import com.artem.streamapp.base.TimeWindowStateStore;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * TODO: Document!
 *
 * @author artem on 22/05/2017.
 */
public class AgentStateStore<W extends TimeWindow> extends TimeWindowStateStore<AgentJVM, W> {

    private static Map<AgentJVM, Lock> locks = new ConcurrentHashMap<>();

    public interface WindowUpdater<W> {
        void update(W window);
    }

    protected AgentStateStore(String storeId, long maxSizeMillis, TypeReference<W> windowType) {
        super(storeId, maxSizeMillis, new TypeReference<AgentJVM>() {}, windowType);
    }

    protected AgentJVM agentJVM() {
        return container instanceof AgentProcessor ? ((AgentProcessor)container).agentJVM : null;
    }

    public void updateWindow(WindowUpdater<W> updater) {
        AgentJVM key = agentJVM();
        W window = getWindow(key);
        updater.update(window);
        Lock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
        lock.lock();
        try {
            W latestWindow = getWindow(key);
            latestWindow.putAll(window);
            putWindow(key, latestWindow);
        } finally {
            lock.unlock();
        }
    }
}
