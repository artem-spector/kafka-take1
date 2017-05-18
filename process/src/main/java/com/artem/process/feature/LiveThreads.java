package com.artem.process.feature;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TODO: Document!
 *
 * @author artem on 18/05/2017.
 */
public class LiveThreads {

    @JsonProperty
    private Map<String, Set<String>> dumps = new HashMap<>();

    public void addThread(String dump, String thread) {
        dumps.computeIfAbsent(dump, key -> new HashSet<>()).add(thread);
    }
}
