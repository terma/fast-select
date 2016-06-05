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

import com.github.terma.fastselect.data.LongData;

import java.util.Map;

/**
 * Math's analog is "a to []". Include min and max.
 * For equal select use {@link IntRequest}
 */
@SuppressWarnings("WeakerAccess")
public class LongBetweenRequest extends ColumnRequest {

    private final long min;
    private final long max;
    private long[] data;

    public LongBetweenRequest(String name, long min, long max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean checkBlock(Block block) {
        Range range = block.ranges.get(column.index);
        return range.max >= min && range.min <= max;
    }

    @Override
    public boolean checkValue(int position) {
        final long value = data[position];
        return value >= min && value <= max;
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        // caching
        data = ((LongData) column.data).data;
    }

    @Override
    public String toString() {
        return "{name: '" + name + "', min: " + min + ", max: " + max + '}';
    }

}
