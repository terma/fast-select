package com.github.terma.fastselect;

import com.github.terma.fastselect.callbacks.ArrayLayoutCallback;

import java.util.ArrayList;
import java.util.List;

public final class DataBlock<T> extends Block<T> {

    public final List<XColumn> columns;
    private final FastSelect<T> fastSelect;

    DataBlock(FastSelect<T> fastSelect, List<FastSelect.Column> columns) {
        this.fastSelect = fastSelect;

        List<XColumn> temp = new ArrayList<>();
        for (FastSelect.Column column : columns) {
            if (column.type == long.class) {
                temp.add(new LongColumn(column.name, column.index));
            } else if (column.type == int.class) {
                temp.add(new IntColumn(column.name, column.index));
            } else if (column.type == short.class) {
                temp.add(new ShortColumn(column.name, column.index));
            } else if (column.type == byte.class) {
                temp.add(new ByteColumn(column.name));
            } else if (column.type == String.class) {
                temp.add(new StringColumn(column.name));
            }
        }

        this.columns = temp;
    }

    @Override
    void add(T row) {
        for (XColumn column : columns) {
            try {
                if (column.type() == long.class) {
                    long v = (long) fastSelect.mhRepo.get(column.name()).invoke(row);
                    ((LongColumn) column).add(v);

                } else if (column.type() == int.class) {
                    int v = (int) fastSelect.mhRepo.get(column.name()).invoke(row);
                    ((IntColumn) column).add(v);

                } else if (column.type() == short.class) {
                    short v = (short) fastSelect.mhRepo.get(column.name()).invoke(row);
                    ((ShortColumn) column).add(v);

                } else if (column.type() == byte.class) {
                    byte v = (byte) fastSelect.mhRepo.get(column.name()).invoke(row);
                    ((ByteColumn) column).add(v);

                } else if (column.type() == String.class) {
                    String v = (String) fastSelect.mhRepo.get(column.name()).invoke(row);
                    ((StringColumn) column).add(v);

                } else {
                    throw new IllegalArgumentException("Doesn't support type: " + column.type() + " for storage!");
                }
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }

    @Override
    boolean isFull() {
        return getSize() == getMaxSize();
    }

    private int getMaxSize() {
        return 1000; // todo be configurable
    }

    @Override
    int getSize() {
        return columns.get(0).size();
    }

    @Override
    void select(AbstractRequest[] where, ArrayLayoutCallback callback) {
        if (columns.isEmpty()) return;

        final int size = getSize();

        for (final AbstractRequest request : where) {
            request.xColumn = columns.get(request.column.index);
        }

        opa:
        for (int i = 0; i < size; i++) {
            for (final AbstractRequest request : where) {
                if (!request.checkValue(i)) continue opa;
            }

            callback.data(i, columns);
        }
    }

}
