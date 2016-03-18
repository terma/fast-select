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

package com.github.terma.fastselect.data;

import java.util.Arrays;

public class LongData implements Data {

    private final int inc;

    public long[] data = new long[16];
    public int size = 0;

    public LongData(int inc) {
        this.inc = inc;
    }

    public void add(long v) {
        if (size == data.length) {
            data = Arrays.copyOf(data, size + inc);
        }
        data[size] = v;
        size++;
    }

    @Override
    public boolean check(int position, int[] values) {
        return Arrays.binarySearch(values, (int) data[position]) >= 0;
    }

    @Override
    public boolean plainCheck(int position, byte[] values) {
        // todo will work when data in int range need to fix that
        int value = (int) data[position];
        return value < values.length && values[value] > 0;
    }

    @Override
    public Object get(int position) {
        return data[position];
    }

    @Override
    public int compare(int position1, int position2) {
        return Long.compare(data[position1], data[position2]);
    }

    @Override
    public void compact() {
        data = Arrays.copyOf(data, size);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int allocatedSize() {
        return data.length;
    }

    @Override
    public long mem() {
        return OBJECT_HEADER_BYTES + REFERENCE_BYTES + INT_BYTES + data.length * LONG_BYTES;
    }

    @Override
    public int inc() {
        return inc;
    }
}
