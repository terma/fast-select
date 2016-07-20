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

import com.github.terma.fastselect.data.StringCompressedByteData;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;

/**
 * SQL analog <code>where lowerCase(STRING_FIELD) like lowerCase('%SUBSTRING%')</code>
 * <p>
 * Data type {@link com.github.terma.fastselect.data.StringCompressedByteData}
 */
@SuppressWarnings("WeakerAccess")
public class StringCompressedByteNoCaseLikeRequest extends ColumnRequest {

    private final String like;

    private byte[] data;

    /**
     * Max size of data dictionary is {@link Byte#MAX_VALUE}
     * So we have init array where non zero
     */
    private byte[] plainSet = new byte[Byte.MAX_VALUE];

    public StringCompressedByteNoCaseLikeRequest(String name, String like) {
        super(name);

        if (like == null) throw new IllegalArgumentException("Can't search null string!");
        this.like = like.toLowerCase();
    }

    @Override
    public boolean checkBlock(Block block) {
        BitSet bitSet = block.columnBitSets.get(column.index);
        boolean p = false;
        for (final byte position : plainSet) {
            p = p | (position >= 0 && bitSet.get(position));
        }
        return p;
    }

    @Override
    public boolean checkValue(int position) {
        byte v = data[position];
        return plainSet[v] >= 0;
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        super.prepare(columnByNames);

        // init all items as non present
        Arrays.fill(plainSet, Byte.MIN_VALUE);

        data = ((StringCompressedByteData) column.data).data.data;

        Map<String, Byte> valueToPosition = ((StringCompressedByteData) column.data).getValueToPosition();
        for (Map.Entry<String, Byte> vp : valueToPosition.entrySet()) {
            if (vp.getKey().toLowerCase().contains(like)) {
                // set real position in dictionary if item should be find
                plainSet[vp.getValue()] = vp.getValue();
            }
        }
    }

    @Override
    public String toString() {
        return "StringCompressedByteNoCaseLikeRequest {name: " + name + ", like: " + like + '}';
    }

}
