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
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * For small {@link Byte} type. Doesn't support Null.
 *
 * @see com.github.terma.fastselect.ByteRequest
 * @see com.github.terma.fastselect.ByteBetweenRequest
 */
public class ByteData implements Data {

    private final int inc;

    public byte[] data;
    public int size;

    public ByteData(int inc) {
        this.size = 0;
        this.inc = inc;
        this.data = new byte[DEFAULT_SIZE];
    }

    public ByteData(ByteData data, byte[] needToCopy) {
        this.inc = data.inc;
        this.data = new byte[data.data.length];
        this.size = needToCopy.length;
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
        while (size > data.length) {
            data = Arrays.copyOf(data, size + inc);
        }
    }

    public void set(int index, byte v) {
        data[index] = v;
    }

    public void add(byte v) {
        if (size == data.length) {
            data = Arrays.copyOf(data, size + inc);
        }
        data[size] = v;
        size++;
    }

    @Override
    public void load(FileChannel fileChannel, int size) throws IOException {
        this.size = size;
        this.data = new byte[size];
        ByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, fileChannel.position(), size);
        buffer.get(data);
        fileChannel.position(fileChannel.position() + buffer.position());
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
        return OBJECT_HEADER_BYTES + REFERENCE_BYTES + INT_BYTES + data.length;
    }

    @Override
    public int inc() {
        return inc;
    }

    @Override
    public Data copy(final byte[] needToCopy) {
        return new ByteData(this, needToCopy);
    }

}
