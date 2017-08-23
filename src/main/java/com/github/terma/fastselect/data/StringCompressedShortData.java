/*
Copyright 2015-2017 Artem Stasiuk

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
import java.util.HashMap;
import java.util.Map;

/**
 * Max possible distinct values {@link Short#MAX_VALUE}
 * <p>
 * To use that type of data field should have type {@link String} and additionally
 * marked by {@link StringCompressedShort}
 * <p>
 * Save and load string as decoded to UTF-8 <code>byte[]</code> representation
 *
 * @see StringCompressedByteData
 * @see StringData
 */
public class StringCompressedShortData implements Data {

    public final ShortData data;
    private final Map<String, Short> valueToPosition;
    private final String[] values;

    public StringCompressedShortData(final int inc) {
        data = new ShortData(inc);
        values = new String[Short.MAX_VALUE];
        valueToPosition = new HashMap<>();
    }

    public StringCompressedShortData(StringCompressedShortData data, byte[] needToCopy) {
        this.data = (ShortData) data.data.copy(needToCopy);
        this.values = data.values;
        this.valueToPosition = data.valueToPosition;
    }

    public Map<String, Short> getValueToPosition() {
        return valueToPosition;
    }

    public short add(String v) {
        Short position = valueToPosition.get(v);
        if (position == null) {
            if (valueToPosition.size() >= Short.MAX_VALUE)
                throw new IllegalArgumentException("Too many (" + Short.MAX_VALUE + ") distinct values!");
            position = (short) valueToPosition.size();
            valueToPosition.put(v, position);
            values[position] = v;
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
        buffer.putInt(values.length);
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
            values[i] = IOUtils.readString(buffer);
            valueToPosition.put(values[i], (short) i);
        }
        data.load("", buffer, size);
    }

    @Override
    public Object get(int position) {
        return values[data.data[position]];
    }

    @Override
    public int compare(int position1, int position2) {
        return values[data.data[position1]].compareTo(values[data.data[position2]]);
    }

    @Override
    public int hashCode(int position) {
        return data.data[position];
    }

    @Override
    public void init(int size) {
        data.init(size);
        values[0] = null;
        valueToPosition.put(null, (short) 0);
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
        return new StringCompressedShortData(this, needToCopy);
    }
}
