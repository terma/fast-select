/*
Copyright 2015-2016 Artem Stasiuk

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
import com.github.terma.fastselect.data.ByteData;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Artem Stasiuk
 * @see MultiGroupCountCallback
 */
@NotThreadSafe
public class TwoByteGroupCountCallback implements ArrayLayoutCallback {

    private final byte[][] counts;
    private final FastSelect.Column first;
    private final FastSelect.Column last;

    public TwoByteGroupCountCallback(final FastSelect.Column groupBy1, final FastSelect.Column groupBy2) {
        this.first = groupBy1;
        this.last = groupBy2;

        counts = new byte[Byte.MAX_VALUE][];
        for (int i = 0; i < Byte.MAX_VALUE; i++) counts[i] = new byte[Byte.MAX_VALUE];
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
        byte firstValue = ((ByteData) first.data).data[position];
        byte lastValue = ((ByteData) last.data).data[position];

        counts[firstValue][lastValue]++;
    }

    public byte[][] getCounters() {
        return counts;
    }

}
