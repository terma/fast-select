package com.github.terma.fastselect;

import java.util.Arrays;
import java.util.BitSet;

public class ShortColumn extends XColumn {

    private final String name;
    private final int index;
    public BitSet bitSet = new BitSet();
    public short[] data = new short[16];
    private int size;

    public ShortColumn(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public void add(short v) {
        data[size] = v;
        size++;

        if (size >= data.length) data = Arrays.copyOf(data, size * 2);

        bitSet.set(v);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Class type() {
        return short.class;
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
