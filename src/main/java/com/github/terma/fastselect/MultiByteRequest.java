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

import java.util.Arrays;
import java.util.Map;

/**
 * SQL analog is <code>where COLUMN_X in (?, ?...)</code>
 * <p>
 * For <code>byte[]</code> data type. Storage implementation is {@link com.github.terma.fastselect.data.MultiByteData}
 */
@SuppressWarnings("WeakerAccess")
public class MultiByteRequest extends ColumnRequest {

    private final byte[] values;
    private MultiByteData data;
    private byte[] realData;

    public MultiByteRequest(final String name, final byte... values) {
        super(name);
        this.values = values;
    }

    @Override
    public boolean checkValue(final int position) {
        int dataStartPosition = data.getDataStart(position);
        int dataEndPosition = data.getDataEnd(position);
        for (int i = dataStartPosition; i < dataEndPosition; i++) {
            if (Arrays.binarySearch(values, realData[i]) >= 0) return true;
        }

        return false;
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        super.prepare(columnByNames);
        Arrays.sort(values);
        data = ((MultiByteData) column.data);
        realData = data.data.data;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " {name: '" + name + "', values: " + Arrays.toString(values) + "}";
    }

}
