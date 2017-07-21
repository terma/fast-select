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

import com.github.terma.fastselect.utils.IOUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Max possible distinct values {@link Integer#MAX_VALUE}
 * <p>
 * To use that type of data field should have type {@link String} and additionally
 * marked by {@link StringCompressedInt}
 * <p>
 * Save and load string as decoded to UTF-8 <code>byte[]</code> representation
 *
 * @see StringCompressedByteData
 * @see StringCompressedShortData
 * @see StringData
 */
public class StringCompressedIntData implements Data {

    public final IntData data;
    private final Map<String, Integer> valueToPosition;
    private final List<String> values;

    public StringCompressedIntData(final int inc) {
        data = new IntData(inc);
        values = new ArrayList<>();
        valueToPosition = new HashMap<>();
    }

    public StringCompressedIntData(StringCompressedIntData data, byte[] needToCopy) {
        this.data = (IntData) data.data.copy(needToCopy);
        this.values = data.values;
        this.valueToPosition = data.valueToPosition;
    }

    public Map<String, Integer> getValueToPosition() {
        return valueToPosition;
    }

    public int add(String v) {
        Integer position = valueToPosition.get(v);
        if (position == null) {
            if (valueToPosition.size() >= Integer.MAX_VALUE)
                throw new IllegalArgumentException("Too many (" + Integer.MAX_VALUE + ") distinct values!");
            position = valueToPosition.size();
            valueToPosition.put(v, position);
            values.add(v);
        }
        data.add(position);
        return position;
    }

    @Override
    public int getDiskSpace() {
        int space = Data.INT_BYTES;
        for (String string : values) {
            space += IOUtils.getStringBytesSize(string);
        }
        return space + data.getDiskSpace();
    }

    @Override
    public void save(final ByteBuffer buffer) throws IOException {
        // todo remove duplication with other compressed data
        buffer.putInt(values.size());
        for (String string : values) {
            IOUtils.writeString(buffer, string);
        }
        data.save(buffer);
    }

    /**
     * <pre>
     *  dictionary-size: int
     *  dictionary-string-0-size: int
     *  dictionary-string-0-data: byte[]
     *  ...
     *  dictionary-string-N-size: int
     *  dictionary-string-N-data: byte[]
     *  byte data: ByteData
     * </pre>
     *
     * @param buffer - b
     * @param size   - count of elements in data (not bytes)
     */
    @Override
    public void load(String dataClass, ByteBuffer buffer, int size) throws IOException {
        int dictionarySize = buffer.getInt();
        for (int i = 0; i < dictionarySize; i++) {
            values.add(IOUtils.readString(buffer));
            valueToPosition.put(values.get(i), i);
        }
        data.load("", buffer, size);
    }

    @Override
    public Object get(int position) {
        return values.get(data.data[position]);
    }

    @Override
    public int compare(int position1, int position2) {
        return values.get(data.data[position1]).compareTo(values.get(data.data[position2]));
    }

    @Override
    public void init(int size) {
        data.init(size);
        values.add(null);
        valueToPosition.put(null, 0);
    }

    @Override
    public void compact() {
        data.compact();
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
        return new StringCompressedIntData(this, needToCopy);
    }
}
