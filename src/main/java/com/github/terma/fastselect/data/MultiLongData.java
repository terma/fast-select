package com.github.terma.fastselect.data;

import java.util.Arrays;

public class MultiLongData implements Data {

    public FastIntData index = new FastIntData();
    public LongData data = new LongData();

    public void add(long[] values) {
        index.add(data.size); // store index of first element of data
        for (long v : values) data.add(v);
    }

    public int getDataStart(int position) {
        return index.data[position];
    }

    public int getDataEnd(int position) {
        return index.size == position + 1 ? data.size : index.data[position + 1];
    }

    @Override
    public boolean check(int position, int[] values) {
        int dataStartPosition = getDataStart(position);
        int dataEndPosition = getDataEnd(position);
        for (int i = dataStartPosition; i < dataEndPosition; i++) {
            if (Arrays.binarySearch(values, (int) data.data[i]) >= 0) return true;
        }
        return false;
    }

    @Override
    public Object get(int position) {
        int start = getDataStart(position);
        int end = getDataEnd(position);
        long[] values = new long[end - start];
        System.arraycopy(data.data, start, values, 0, values.length);
        return values;
    }

    @Override
    public int size() {
        return index.size();
    }
}
