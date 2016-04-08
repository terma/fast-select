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

import com.github.terma.fastselect.callbacks.ArrayLayoutCallback;
import com.github.terma.fastselect.callbacks.ArrayLayoutLimitCallback;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public abstract class Block {

    private final static long MIN_COLUMN_BIT = 0;

    protected final List<BitSet> columnBitSets = new ArrayList<>();
    protected final List<Range> ranges = new ArrayList<>();

    abstract int free();

    void setColumnBitSet(FastSelect.Column column, int bit) {
        // todo implement indexing for negative values, currently just use direct scan
        if (bit >= MIN_COLUMN_BIT) {
            columnBitSets.get(column.index).set(bit);
        }
    }

    abstract void add(List dataToAdd, int addFrom, int addTo);

    abstract void select(Request[] where, ArrayLayoutCallback callback);

    abstract int blockTouch(Request[] where);

    abstract void select(Request[] where, ArrayLayoutLimitCallback callback);

}
