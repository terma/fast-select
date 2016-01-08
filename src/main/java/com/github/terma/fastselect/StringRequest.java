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

import com.github.terma.fastselect.data.StringData;

import java.util.Arrays;
import java.util.BitSet;

public class StringRequest extends AbstractRequest {

    private final byte[] bytes;

    public StringRequest(String name, String value) {
        super(name);
        bytes = value.getBytes();
    }

    @Override
    boolean inBlock(final BitSet bitSet) {
        // todo try to use block info for string filter, now just always goto full scan
        return true;
    }

    @Override
    boolean checkValue(int position) {
        StringData data = (StringData) column.data;
        byte[] value = data.getRaw(position);
        return Arrays.equals(bytes, value);
    }

    @Override
    void prepare() {
    }

    @Override
    public String toString() {
        return "StringRequest {name: " + name + ", value: " + new String(bytes) + '}';
    }

}
