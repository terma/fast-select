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

/**
 * <h3>Base callback</h3>
 * Next example show how to get sum by specific column of filtered data
 * <pre>
 * class SumCallback implements ArrayLayoutCallback {
 *     private final long[] values;
 *     private long result;
 *
 *     public SumCallback(final FastSelect&lt;T&gt; fastSelect) {
 *         this.values = ((LongData) fastSelect.getColumnsByNames().get("value").data).data;
 *     }
 *
 *     public void data(int position) {
 *         result += values[position];
 *     }
 *
 *     public long getResult() {
 *         return result;
 *     }
 * }
 * </pre>
 *
 * @see FastSelect
 * @see Callback
 */
public interface ArrayLayoutCallback {

    /**
     * During select in {@link com.github.terma.fastselect.FastSelect}
     * this method will be called for each row which accept filter criteria.
     *
     * @param position from <code>0</code> to <code>{@link FastSelect#size()} - 1</code>
     */
    void data(int position);

}
