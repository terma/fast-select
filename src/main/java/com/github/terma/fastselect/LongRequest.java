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

import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class LongRequest extends AbstractRequest {

    private final long[] values; // after .prepared() sorted by ASC
    private long[] data; // cache

    public LongRequest(String name, long[] values) {
        super(name);
        this.values = values;
    }

    @Override
    public boolean checkBlock(Range range) {
        return values[0] <= range.max && values[values.length - 1] >= range.min;
    }

    @Override
    public boolean checkValue(int position) {
        long value = data[position];
        return values[0] <= value && values[values.length - 1] >= value && Arrays.binarySearch(values, value) > -1;
    }

    @Override
    public void prepare() {
        // caching
        data = ((LongData) column.data).data;

        // prepare
        Arrays.sort(values);
    }

    @Override
    public String toString() {
        return "{name: " + name + ", values: " + Arrays.toString(values) + '}';
    }

}
