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

public class MultiShortData implements Data {

    public IntData index = new IntData();
    public ShortData data = new ShortData();

    public void add(short[] values) {
        index.add(data.size); // store index of first element of data
        for (short v : values) data.add(v);
    }

    public int getDataStart(int position) {
        return index.data[position];
    }

    public int getDataEnd(int position) {
        return index.size == position + 1 ? data.size : index.data[position + 1];
    }

    @Override
    public boolean check(int position, int[] values) {
        int dataStartPosition = getDataStart(position);
        int dataEndPosition = getDataEnd(position);
        for (int i = dataStartPosition; i < dataEndPosition; i++) {
            if (Arrays.binarySearch(values, (int) data.data[i]) >= 0) return true;
        }
        return false;
    }

    @Override
    public boolean plainCheck(int position, byte[] values) {
        int dataStartPosition = getDataStart(position);
        int dataEndPosition = getDataEnd(position);
        for (int i = dataStartPosition; i < dataEndPosition; i++) {
            int value = data.data[i];
            if (value < values.length && values[value] > 0) return true;
        }
        return false;
    }

    @Override
    public Object get(int position) {
        int start = getDataStart(position);
        int end = getDataEnd(position);
        short[] values = new short[end - start];
        System.arraycopy(data.data, start, values, 0, values.length);
        return values;
    }


    @Override
    public int size() {
        return index.size();
    }
}
