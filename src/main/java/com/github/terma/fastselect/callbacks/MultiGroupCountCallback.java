/*
Copyright 2015 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.fastselect.callbacks;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.Request;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashMap;
import java.util.Map;

/**
 * Analog of SQL expression:
 * <pre>select count(COLUMNS) from TABLE where CONDITION group by COLUMNS (at least 2)</pre>
 *
 * Result will be map with submap as child and counter leaf map.
 *
 * For example (columns and result object structure):
 * <dl>
 * <dt>COLUMN_A, COLUMN_B</dt>
 * <dd><pre>Map&lt;Integer, Map&lt;Integer, Integer&gt;&gt;</pre></dd>
 * <dt>COLUMN_A, COLUMN_B, COLUMN_C</dt>
 * <dd><pre>Map&lt;Integer, Map&lt;Integer, Map&lt;Integer, Integer&gt;&gt;&gt;</pre></dd>
 * </dl>
 *
 * Calling {@link FastSelect#select(Request[], ArrayLayoutCallback)} twice with same instance is ok.
 * Result will be counter twice.
 *
 * @author Artem Stasiuk
 * @see GroupCountCallback
 */
@NotThreadSafe
public class MultiGroupCountCallback implements ArrayLayoutCallback {

    private final Map<Integer, Object> counters = new HashMap<>();
    private final FastSelect.Column first;
    private final FastSelect.Column[] middle;
    private final FastSelect.Column last;

    public MultiGroupCountCallback(final FastSelect.Column... groupBy) {
        this.first = groupBy[0];
        this.last = groupBy[groupBy.length - 1];

        this.middle = new FastSelect.Column[groupBy.length - 2];
        System.arraycopy(groupBy, 1, middle, 0, middle.length);
    }

    @SuppressWarnings("unchecked")
    private static Map<Integer, Object> getNestedMapMapOrNew(Map<Integer, Object> map, int value) {
        Map<Integer, Object> item = (Map<Integer, Object>) map.get(value);
        if (item == null) {
            item = new HashMap<>();
            map.put(value, item);
        }
        return item;
    }

    @Override
    public void data(final int position) {
        Map<Integer, Object> counter = getNestedMapMapOrNew(counters, first.valueAsInt(position));

        for (FastSelect.Column m : middle) {
            counter = getNestedMapMapOrNew(counter, m.valueAsInt(position));
        }

        final int value = last.valueAsInt(position);
        final Integer c = (Integer) counter.get(value);
        if (c == null) counter.put(value, 1);
        else counter.put(value, c + 1);
    }

    public Map<Integer, Object> getCounters() {
        return counters;
    }

}
