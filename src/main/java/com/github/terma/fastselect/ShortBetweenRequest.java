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

import com.github.terma.fastselect.data.ShortData;

import java.util.Map;

/**
 * Math's analog is "a to []". Include min and max.
 * For equal select use {@link ShortRequest}
 */
@SuppressWarnings("WeakerAccess")
public class ShortBetweenRequest extends ColumnRequest {

    private final short min;
    private final short max;
    private short[] data;

    public ShortBetweenRequest(String name, short min, short max) {
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
        final short value = data[position];
        return value >= min && value <= max;
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        // caching
        data = ((ShortData) column.data).data;
    }

    @Override
    public String toString() {
        return "{name: '" + name + "', min: " + min + ", max: " + max + '}';
    }

}
