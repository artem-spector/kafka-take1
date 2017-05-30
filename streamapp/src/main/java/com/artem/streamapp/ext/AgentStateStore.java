package com.artem.streamapp.ext;

import com.artem.server.AgentJVM;
import com.artem.streamapp.base.TimeWindow;
import com.artem.streamapp.base.TimeWindowStateStore;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * TODO: Document!
 *
 * @author artem on 22/05/2017.
 */
public class AgentStateStore<W extends TimeWindow> extends TimeWindowStateStore<AgentJVM, W> {

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
        W latestWindow = getWindow(key);
        latestWindow.putAll(window);
        putWindow(key, latestWindow);
    }
}
