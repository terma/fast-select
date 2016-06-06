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

import com.github.terma.fastselect.data.MultiIntData;
import com.github.terma.fastselect.data.MultiLongData;

import java.util.Arrays;
import java.util.Map;

/**
 * For {code}int[]{code} data type. Storage implementation is {@link com.github.terma.fastselect.data.MultiIntData}
 * <p>
 * SQL analog is {code}where COLUMN_X in (?, ?...){code}
 */
@SuppressWarnings("WeakerAccess")
public class MultiIntRequest extends ColumnRequest {

    private final long[] values;
    private MultiIntData data;
    private int[] realData;

    public MultiIntRequest(final String name, final long... values) {
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

        data = ((MultiIntData) column.data);
        realData = data.data.data;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " {name: '" + name + "', values: " + Arrays.toString(values) + "}";
    }

}
