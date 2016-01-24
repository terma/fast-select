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

import java.util.Arrays;

public class LongRequest extends AbstractRequest {

    private final long[] values;
    private long[] cachedData;

    public LongRequest(String name, long[] values) {
        super(name);
        this.values = values;
    }

    @Override
    boolean inBlock(XColumn column) {
        LongColumn c = (LongColumn) column;
        cachedData = c.data;

        return c.max >= values[0] && c.min <= values[values.length - 1];
    }

    @Override
    boolean checkValue(int position) {
        return Arrays.binarySearch(values, cachedData[position]) > -1;
    }

    @Override
    void prepare() {
        Arrays.sort(values);
    }

    @Override
    public String toString() {
        return "{name: " + name + ", values: " + Arrays.toString(values) + '}';
    }

}
