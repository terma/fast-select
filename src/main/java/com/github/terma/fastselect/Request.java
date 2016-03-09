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
import java.util.BitSet;

/**
 * Deprecated Class.
 * <p>
 * Please use {@link AbstractRequest} as base class. And any child of it for specific select criteria.
 */
@Deprecated
public class Request extends AbstractRequest {

    private final int[] values;

    private byte[] plainValues;

    public Request(String name, int[] values) {
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
        if (plainValues != null) {
            return column.data.plainCheck(position, plainValues);
        } else {
            return column.data.check(position, values);
        }
    }

    @Override
        // todo implement search by array if direct index can't be used Arrays.sort(condition.values);
    void prepare() {
        int max = values[0];
        for (int i = 1; i < values.length; i++)
            if (values[i] > max) max = values[i];

        plainValues = new byte[max + 1];
        for (int value : values) plainValues[value] = 1;
    }

    @Override
    public String toString() {
        return "{name: " + name + ", values: " + Arrays.toString(values) + '}';
    }

}
