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

import com.github.terma.fastselect.data.MultiByteData;
import com.github.terma.fastselect.data.StringData;

import java.util.Map;

/**
 * SQL analog <code>where STRING_FIELD = '???'</code>
 * Exact select. For like or case insensitive use {@link StringLikeRequest} and {@link StringNoCaseLikeRequest}
 */
public class StringRequest extends ColumnRequest {

    private final byte[] bytes;

    private MultiByteData data;
    private byte[] byteData;

    public StringRequest(String name, String value) {
        super(name);
        bytes = value.getBytes();
    }

    @Override
    public boolean checkValue(int position) {
        int start = data.getDataStart(position);
        int end = data.getDataEnd(position);
        int l = end - start;
        if (l != bytes.length) return false;
        for (int i = 0; i < l; i++)
            if (bytes[i] != byteData[start + i]) return false;
        return true;
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        super.prepare(columnByNames);
        data = ((StringData) column.data).getData();
        byteData = data.data.data;
    }

    @Override
    public String toString() {
        return name + " = '" + new String(bytes) + "'";
    }

}
