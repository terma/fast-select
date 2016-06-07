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

package com.github.terma.fastselect;

import com.github.terma.fastselect.data.ByteData;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;

/**
 * SQL analog is <code>COLUMN_X in (A, B, ...)</code>
 *
 * @see ByteData
 */
public class ByteRequest extends ColumnRequest {

    private final int[] values;

    private byte[] plainSet;
    private byte[] data;

    public ByteRequest(String name, int... values) {
        super(name);
        this.values = values;
    }

    @Override
    public boolean checkBlock(Block block) {
        BitSet bitSet = block.columnBitSets.get(column.index);
        boolean p = false;
        for (final int value : values) {
            p = p | bitSet.get(value);
        }
        return p;
    }

    @Override
    public boolean checkValue(int position) {
        byte v = data[position];
        return v < plainSet.length && plainSet[v] > 0;
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        super.prepare(columnByNames);

        // cache data
        data = ((ByteData) column.data).data;

        // plain
        int max = 0;
        for (int b : values) max = Math.max(max, b);

        plainSet = new byte[max + 1];
        for (int b : values) plainSet[b] = 1;
    }

    @Override
    public String toString() {
        return "ByteRequest {name: '" + name + "', values: " + Arrays.toString(values) + '}';
    }

}
