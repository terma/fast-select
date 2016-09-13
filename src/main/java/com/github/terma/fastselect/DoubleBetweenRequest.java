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

import com.github.terma.fastselect.data.DoubleData;

import java.util.Map;

/**
 * SQL analog is <code>COLUMN_X &gt;= MIN and COLUMN_X &lt;= MAX</code>
 * <p>
 * Math's analog is "a to []". Include min and max.
 * <p>
 * WARNING: Don't use {@link Double#MIN_VALUE}
 * to build condition no more some value like: <code>COLUMN_X &gt;= 1.23</code>
 * It means small POSITIVE value as possible not NEGATIVE! Instead of that please use:
 * <p>
 * <code>new DoubleBetweenRequest("COLUMN_NAME", -Double.MAX_VALUE, MAX_UP_BORDER)</code>
 * <p>
 * That version of request use full scan. It doesn't support fast skip for block based
 * on block statistic. If you can use other data types for example {@link LongBetweenRequest}
 * which can delivery much faster search.
 *
 * @see LongBetweenRequest
 * @see IntBetweenRequest
 * @see ShortBetweenRequest
 * @see ByteBetweenRequest
 */
@SuppressWarnings("WeakerAccess")
public class DoubleBetweenRequest extends ColumnRequest {

    private final double min;
    private final double max;
    private double[] data;

    public DoubleBetweenRequest(String name, double min, double max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean checkValue(int position) {
        final double value = data[position];
        return value >= min && value <= max;
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        super.prepare(columnByNames);
        // caching
        data = ((DoubleData) column.data).data;
    }

    @Override
    public String toString() {
        return "{name: '" + name + "', min: " + min + ", max: " + max + '}';
    }

}
