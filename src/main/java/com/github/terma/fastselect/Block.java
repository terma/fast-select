package com.github.terma.fastselect;

import com.github.terma.fastselect.callbacks.ArrayLayoutCallback;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public abstract class Block<T> {

    final List<BitSet> columnBitSets = new ArrayList<>();

    abstract boolean isFull();

//    void setColumnBitSet(FastSelect.Column column, int bit) {
//        if (bit >= FastSelect.MIN_COLUMN_BIT && bit <= FastSelect.MAX_COLUMN_BIT) {
//            columnBitSets.get(column.index).set(bit);
//        } else {
//            // todo implement indexing for negative values, currently just use direct scan
//        }
//    }

    abstract int getSize();

    abstract void add(T row);

    abstract void select(AbstractRequest[] where, ArrayLayoutCallback callback);

}
