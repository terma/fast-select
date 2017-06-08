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

/**
 * SQL analog <code>where STRING_FIELD = '???'</code>
 * Exact select. For like or case insensitive use {@link StringLikeRequest} and {@link StringNoCaseLikeRequest}
 */
public class StringRequest extends ColumnRequest {

    private final byte[] bytes;

    public StringRequest(String name, String value) {
        super(name);
        bytes = value.getBytes();
    }

    @Override
    public boolean checkValue(int position) {
        StringData data = (StringData) column.data;
        byte[] value = data.getRaw(position);
        return Arrays.equals(bytes, value);
    }

    @Override
    public String toString() {
        return name + " = '" + new String(bytes) + "'";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringRequest that = (StringRequest) o;
        return name.equals(that.name) && Arrays.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(bytes);
        result = 31 * result + name.hashCode();
        return result;
    }

}
