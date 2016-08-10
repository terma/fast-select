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

import com.github.terma.fastselect.data.StringCompressedIntData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * SQL analog <code>where lowerCase(STRING_FIELD) like lowerCase('%SUBSTRING%')</code>
 * <p>
 * Data type {@link com.github.terma.fastselect.data.StringCompressedIntData}
 */
@SuppressWarnings("WeakerAccess")
public class StringCompressedIntNoCaseLikeRequest extends ColumnRequest {

    private final String like;

    private int[] data;
    private List<Integer> addresses;

    public StringCompressedIntNoCaseLikeRequest(String name, String like) {
        super(name);

        if (like == null) throw new IllegalArgumentException("Can't search null string!");
        this.like = like.toLowerCase();
    }

    @Override
    public boolean checkBlock(Block block) {
        Range range = block.ranges.get(column.index);
        return !addresses.isEmpty() && addresses.get(0) <= range.max && addresses.get(addresses.size() - 1) >= range.min;
    }

    @Override
    public boolean checkValue(int position) {
        int v = data[position];
        return Collections.binarySearch(addresses, v) > -1;
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        super.prepare(columnByNames);

        data = ((StringCompressedIntData) column.data).data.data;

        addresses = new ArrayList<>();
        Map<String, Integer> valueToPosition = ((StringCompressedIntData) column.data).getValueToPosition();
        for (Map.Entry<String, Integer> vp : valueToPosition.entrySet()) {
            if (vp.getKey().toLowerCase().contains(like)) {
                addresses.add(vp.getValue());
            }
        }

        Collections.sort(addresses);
    }

    @Override
    public String toString() {
        return "StringCompressedByteNoCaseLikeRequest {name: " + name + ", like: " + like + '}';
    }

}
