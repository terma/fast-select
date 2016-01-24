package com.github.terma.fastselect;

import java.util.Arrays;

public class LongColumn extends XColumn {

    private final String name;
    private final int index;
    public long max;
    public long min;
    public long[] data = new long[16];
    private int size;

    public LongColumn(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public void add(long v) {
        data[size] = v;
        size++;

        if (size >= data.length) data = Arrays.copyOf(data, size * 2);

        max = Math.max(max, v);
        min = Math.min(min, v);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Class type() {
        return long.class;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Object get(int position) {
        return data[position];
    }
}
