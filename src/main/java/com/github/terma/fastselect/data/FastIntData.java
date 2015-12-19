package com.github.terma.fastselect.data;

import java.util.Arrays;

public class FastIntData implements Data {

    public int[] data = new int[16];
    public int size = 0;

    public void add(int v) {
        if (size == data.length) {
            data = Arrays.copyOf(data, size + 100000);
        }
        data[size] = v;
        size++;
    }

    @Override
    public boolean check(int position, int[] values) {
        return Arrays.binarySearch(values, data[position]) >= 0;
    }

    @Override
    public Object get(int position) {
        return data[position];
    }

    @Override
    public int size() {
        return size;
    }

}
