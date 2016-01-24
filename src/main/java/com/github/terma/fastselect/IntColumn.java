package com.github.terma.fastselect;

import java.util.Arrays;

public class IntColumn extends XColumn {

    private final String name;
    private final int index;
    public int max;
    public int min;
    public int[] data = new int[16];
    private int size;

    public IntColumn(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public void add(int v) {
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
        return int.class;
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
