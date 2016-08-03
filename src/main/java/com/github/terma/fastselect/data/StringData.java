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

import java.nio.channels.FileChannel;

public class StringData implements Data {

    private static final byte[] ZERO = new byte[0];

    private final MultiByteData data;

    public StringData(final int inc) {
        data = new MultiByteData(inc);
    }

    public StringData(StringData data, byte[] needToCopy) {
        this.data = (MultiByteData) data.data.copy(needToCopy);
    }

    public void add(String v) {
        final byte[] bytes = v == null ? ZERO : v.getBytes();
        data.add(bytes);
    }

    @Override
    public void load(FileChannel fileChannel, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(int position) {
        final byte[] bytes = getRaw(position);
        return new String(bytes);
    }

    @Override
    public int compare(int position1, int position2) {
        return ((String) get(position1)).compareTo((String) get(position2));
    }

    @Override
    public void compact() {
        data.compact();
    }

    public byte[] getRaw(int position) {
        return (byte[]) data.get(position);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public int allocatedSize() {
        return data.allocatedSize();
    }

    @Override
    public long mem() {
        return data.mem();
    }

    @Override
    public int inc() {
        return data.inc();
    }

    @Override
    public Data copy(byte[] needToCopy) {
        return new StringData(this, needToCopy);
    }
}
