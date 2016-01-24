package com.github.terma.fastselect;

import java.util.Arrays;
import java.util.BitSet;

public class ByteColumn extends XColumn {

    private final String name;
    public BitSet bitSet = new BitSet();
    public byte[] data = new byte[16];
    private int size;

    public ByteColumn(String name) {
        this.name = name;
    }

    public void add(byte v) {
        data[size] = v;
        size++;

        if (size >= data.length) data = Arrays.copyOf(data, size * 2);

        if (v >= 0) bitSet.set(v);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Class type() {
        return byte.class;
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
