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

import com.github.terma.fastselect.data.MultiByteData;
import com.github.terma.fastselect.data.StringData;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;

/**
 * SQL analog <code>where STRING_FIELD in ('', ''...)</code>
 *
 * @see StringNoCaseLikeRequest
 * @see StringRequest
 */
@SuppressWarnings("WeakerAccess")
public class StringMultipleRequest extends ColumnRequest {

    private final byte[][] valuesBytes;

    private MultiByteData data;
    private byte[] byteData;

    public StringMultipleRequest(String name, String... values) {
        super(name);
        valuesBytes = new byte[values.length][];
        for (int i = 0; i < values.length; i++) valuesBytes[i] = values[i].getBytes();
    }

    @Override
    public boolean checkBlock(final Block block) {
        BitSet bitSet = block.columnBitSets.get(column.index);
//        boolean p = true;
        for (byte[] bytes : valuesBytes) {
            boolean p = true;
            for (final byte value : bytes) p = p & bitSet.get(value);
            if (p) return true;
        }
        return false;
    }

    @Override
    public boolean checkValue(int position) {
        int start = data.getDataStart(position);
        int end = data.getDataEnd(position);
        int l = end - start;

        values:
        for (final byte[] bytes : valuesBytes) {
            if (l != bytes.length) continue;
            for (int j = 0; j < l; j++)
                if (bytes[j] != byteData[start + j]) continue values;
            return true;
        }
        return false;
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        super.prepare(columnByNames);
        data = ((StringData) column.data).getData();
        byteData = data.data.data;
    }

    @Override
    public String toString() {
        String[] strings = new String[valuesBytes.length];
        for (int i = 0; i < valuesBytes.length; i++) strings[i] = new String(valuesBytes[i]);
        return "StringMultipleRequest {name: '" + name + "', in: " + Arrays.toString(strings) + "}";
    }

}
