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

import java.util.Arrays;
import java.util.BitSet;

public class ShortRequest extends AbstractRequest {

    private final int[] values;

    private BitSet plainSet;
    private short[] data;

    public ShortRequest(String name, int[] values) {
        super(name);
        this.values = values;
    }

    @Override
    boolean inBlock(BitSet bitSet) {
        boolean p = false;
        for (final int value : values) {
            p = p | bitSet.get(value);
        }
        return p;
    }

    @Override
    boolean checkValue(int position) {
        return plainSet.get(data[position]);
    }

    @Override
    void prepare() {
        // cache
        data = ((ShortData) column.data).data;

        // plain
        plainSet = new BitSet();
        for (int i = 0; i < values.length; i++) plainSet.set(values[i]);
    }

    @Override
    public String toString() {
        return "ShortRequest {name: " + name + ", values: " + Arrays.toString(values) + '}';
    }

}
