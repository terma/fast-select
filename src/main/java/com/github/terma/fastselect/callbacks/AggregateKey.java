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

import com.github.terma.fastselect.data.Data;

/**
 * @author Artem Stasiuk
 * @see AggregateCallback
 * @see Aggregator
 * @since 3.2.0
 */
public class AggregateKey {

    private final Data[] datas;
    private final int position;

    /**
     * We use that class for aggregation based on {@link java.util.HashMap}
     * because of that we can assume that {@link AggregateKey#hashCode()} will be called at least
     * once per lifecycle of instance so we can cache that value and avoid recalculation of hashCode
     */
    private final int hashCode;

    AggregateKey(Data[] datas, final int position) {
        this.datas = datas;
        this.position = position;

        int tempHashCode = 1;
        for (Data data : datas) tempHashCode += 31 * tempHashCode + data.hashCode(position);
        this.hashCode = tempHashCode;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        final AggregateKey key = (AggregateKey) o;
        for (final Data data : datas) {
            if (data.compare(position, key.position) != 0) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Data data : datas) sb.append(data.get(position)).append('|');
        return sb.toString();
    }

    public Data[] getDatas() {
        return datas;
    }

    public int getPosition() {
        return position;
    }

}
