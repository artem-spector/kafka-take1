package com.artem.process;

import com.artem.process.feature.TimeWindow;
import org.junit.Test;

import java.util.NavigableMap;

import static org.junit.Assert.assertEquals;

/**
 * TODO: Document!
 *
 * @author artem on 18/05/2017.
 */
public class TimeWindowTest {

    @Test
    public void testSize() {
        long maxSizeMs = 10;
        TimeWindow<String> window = new TimeWindow<>(maxSizeMs);
        int first = 1;
        int last = 100;
        for (int i = first; i <= last; i++) {
            window.putValue(i, "V" + i);
        }

        NavigableMap<Long, String> values = window.getValues(0, Long.MAX_VALUE);
        assertEquals(window.toString(), maxSizeMs + 1, values.size());
        assertEquals(window.toString(), last, (long)values.lastKey());
        assertEquals(window.toString(), last - maxSizeMs, (long)values.firstKey());
    }

    @Test
    public void testSubset() {
        long maxSizeMs = 100;
        TimeWindow<String> window = new TimeWindow<>(maxSizeMs);
        int first = 30;
        int last = first + (int) maxSizeMs;
        for (int i = first; i <= last; i++) {
            window.putValue(i, "V" + i);
        }

        assertEquals(maxSizeMs + 1, window.getValues(first, last).size());
        assertEquals(maxSizeMs + 1, window.getValues(first - 10, last + 10).size());
        assertEquals(maxSizeMs - 1, window.getValues(first + 1, last - 1).size());
        assertEquals(0, window.getValues(first - 2, first - 1).size());
        assertEquals(0, window.getValues(last + 1, last + 2).size());

    }
}
