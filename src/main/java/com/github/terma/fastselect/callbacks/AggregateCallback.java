/*
Copyright 2015-2017 Artem Stasiuk

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

package com.github.terma.fastselect.callbacks;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.data.Data;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashMap;
import java.util.Map;

/**
 * Analog of SQL expression:
 * <pre>select [user function](COLUMN, ...) from TABLE where CONDITION group by COLUMN, ... (at least 2)</pre>
 *
 * Result will be map of aggregate object and {@link AggregateKey}.
 *
 * Calling {@link FastSelect#select(com.github.terma.fastselect.Request[], ArrayLayoutCallback)} twice with same instance is ok.
 * Result will be counter twice.
 *
 * @author Artem Stasiuk
 * @see GroupCountCallback
 * @see MultiGroupCountCallback
 * @see Aggregator
 * @since 3.2.0
 */
@NotThreadSafe
public class AggregateCallback<T> implements ArrayLayoutCallback {

    private final Map<AggregateKey, T> data = new HashMap<>();
    private final Data[] datas;
    private final Aggregator<T> aggregator;

    public AggregateCallback(final Aggregator<T> aggregator, final FastSelect.Column... groupBy) {
        if (aggregator == null) throw new NullPointerException("Please specify aggregator!");
        this.aggregator = aggregator;
        this.datas = new Data[groupBy.length];
        for (int i = 0; i < groupBy.length; i++) datas[i] = groupBy[i].data;
    }

    public AggregateCallback(final Aggregator<T> aggregator, final Data... datas) {
        if (aggregator == null) throw new NullPointerException("Please specify aggregator!");
        this.aggregator = aggregator;
        this.datas = datas;
    }

    @Override
    public void data(final int position) {
        final AggregateKey key = new AggregateKey(datas, position);
        T value = data.get(key);

        if (value == null) data.put(key, aggregator.create(position));
        else aggregator.aggregate(value, position);
    }

    public Map<AggregateKey, T> getResult() {
        return data;
    }

}
