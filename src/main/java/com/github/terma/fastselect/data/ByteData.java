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

public class ByteData implements Data {

    public byte[] data = new byte[16];
    public int size = 0;

    public void add(byte v) {
        if (size == data.length) {
            data = Arrays.copyOf(data, size + INC);
        }
        data[size] = v;
        size++;
    }

    @Override
    public boolean check(int position, int[] values) {
        return Arrays.binarySearch(values, data[position]) >= 0;
    }

    @Override
    public boolean plainCheck(int position, byte[] values) {
        byte value = data[position];
        return value < values.length && values[value] > 0;
    }

    @Override
    public Object get(int position) {
        return data[position];
    }

    @Override
    public int compare(int position1, int position2) {
        return data[position1] - data[position2];
    }

    @Override
    public int size() {
        return size;
    }

}
