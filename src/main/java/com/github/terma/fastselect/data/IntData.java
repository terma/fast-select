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

package com.github.terma.fastselect.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class IntData implements Data {

    private final int inc;

    public int[] data;
    public int size;

    public IntData(int inc) {
        this.inc = inc;
        this.data = new int[16];
        this.size = 0;
    }

    public IntData(IntData data, byte[] needToCopy) {
        this.inc = data.inc;
        this.size = needToCopy.length;
        this.data = new int[data.data.length];
        int c = 0;
        for (int i = 0; i < needToCopy.length; i++) {
            if (needToCopy[i] == 1) {
                this.data[c] = data.data[i];
                c++;
            }
        }
    }

    public void allocate(int additionalSize) {
        size += additionalSize;
        while (size > data.length) data = Arrays.copyOf(data, size + inc);
    }

    public void set(int index, int v) {
        data[index] = v;
    }

    public void add(int v) {
        if (size == data.length) {
            data = Arrays.copyOf(data, size + inc);
        }
        data[size] = v;
        size++;
    }

    @Override
    public int getDiskSpace() {
        return Data.INT_BYTES * size;
    }

    @Override
    public void save(final ByteBuffer buffer) throws IOException {
        buffer.asIntBuffer().put(data, 0, size);
        buffer.position(buffer.position() + Data.INT_BYTES * size);
    }

    public void load(FileChannel fileChannel, int size) throws IOException {
        this.size = size;
        this.data = new int[size];
        IntBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, fileChannel.position(), INT_BYTES * size).asIntBuffer();
        buffer.get(data);
        fileChannel.position(fileChannel.position() + INT_BYTES * buffer.position());
    }

    @Override
    public void load(String dataClass, ByteBuffer buffer, int size) throws IOException {
        this.size = size;
        this.data = new int[size];
        buffer.asIntBuffer().get(data);
        buffer.position(Data.INT_BYTES * size);
    }

    @Override
    public Object get(int position) {
        return data[position];
    }

    @Override
    public int compare(int position1, int position2) {
        return data[position1] - data[position2];
    }

    @Override
    public void compact() {
        data = Arrays.copyOf(data, size);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int allocatedSize() {
        return data.length;
    }

    @Override
    public long mem() {
        return OBJECT_HEADER_BYTES + REFERENCE_BYTES + INT_BYTES + data.length * INT_BYTES;
    }

    @Override
    public int inc() {
        return inc;
    }

    @Override
    public Data copy(final byte[] needToCopy) {
        return new IntData(this, needToCopy);
    }

}
