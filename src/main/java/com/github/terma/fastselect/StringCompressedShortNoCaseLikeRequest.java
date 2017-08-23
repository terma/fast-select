/*
Copyright 2015-2017 Artem Stasiuk

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

import com.github.terma.fastselect.data.StringCompressedShortData;

import java.util.BitSet;
import java.util.Map;

/**
 * SQL analog <code>where lowerCase(STRING_FIELD) like lowerCase('%SUBSTRING%')</code>
 * <p>
 * Data type {@link com.github.terma.fastselect.data.StringCompressedShortData}
 */
@SuppressWarnings("WeakerAccess")
public class StringCompressedShortNoCaseLikeRequest extends ColumnRequest {

    private final String like;

    private short[] data;
    private BitSet plainSet;

    public StringCompressedShortNoCaseLikeRequest(String name, String like) {
        super(name);

        if (like == null) throw new IllegalArgumentException("Can't search null string!");
        this.like = like.toLowerCase();
    }

    @Override
    public boolean checkBlock(Block block) {
        BitSet bitSet = block.columnBitSets.get(column.index);
        return bitSet.intersects(plainSet);
    }

    @Override
    public boolean checkValue(int position) {
        return plainSet.get(data[position]);
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        super.prepare(columnByNames);

        data = ((StringCompressedShortData) column.data).data.data;

        plainSet = new BitSet();
        Map<String, Short> valueToPosition = ((StringCompressedShortData) column.data).getValueToPosition();
        for (Map.Entry<String, Short> vp : valueToPosition.entrySet()) {
            if (like.isEmpty() && vp.getKey() == null || vp.getKey() != null && vp.getKey().toLowerCase().contains(like)) {
                plainSet.set(vp.getValue());
            }
        }
    }

    @Override
    public String toString() {
        return "StringCompressedShortNoCaseLikeRequest {name: " + name + ", like: " + like + '}';
    }

}
