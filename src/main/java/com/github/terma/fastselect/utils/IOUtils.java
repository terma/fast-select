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
package com.github.terma.fastselect.utils;

import com.github.terma.fastselect.data.Data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public final class IOUtils {

    private IOUtils() {
        throw new UnsupportedOperationException("Just util class no instances!");
    }

    public static void writeInt(final FileChannel fileChannel, final int value) throws IOException {
        fileChannel.write((ByteBuffer) ByteBuffer.allocate(Data.INT_BYTES).putInt(value).flip());
    }

    public static void writeLong(FileChannel fileChannel, long value) throws IOException {
        fileChannel.write((ByteBuffer) ByteBuffer.allocate(Data.LONG_BYTES).putLong(value).flip());
    }

    public static int readInt(final FileChannel fileChannel) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(Data.INT_BYTES);
        fileChannel.read(buffer);
        buffer.flip();
        return buffer.getInt();
    }

    public static int readInt(final ByteBuffer buffer) throws IOException {
        return buffer.getInt();
    }

    public static long readLong(final FileChannel fileChannel) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(Data.LONG_BYTES);
        fileChannel.read(buffer);
        buffer.flip();
        return buffer.getLong();
    }

    public static void writeString(final FileChannel fileChannel, final String value) throws IOException {
        if (value != null) {
            byte[] b = Utf8Utils.stringToBytes(value);
            writeInt(fileChannel, b.length);
            fileChannel.write(ByteBuffer.wrap(b));
        } else {
            writeInt(fileChannel, -1);
        }
    }

    public static void writeString(final ByteBuffer buffer, final String value) throws IOException {
        if (value != null) {
            byte[] b = Utf8Utils.stringToBytes(value);
            buffer.putInt(b.length);
            buffer.put(b);
        } else {
            buffer.putInt(-1);
        }
    }

    public static int getStringBytesSize(final String value) {
        if (value != null) {
            byte[] b = Utf8Utils.stringToBytes(value);
            return Data.INT_BYTES + b.length;
        } else {
            return Data.INT_BYTES;
        }
    }

    public static String readString(FileChannel fileChannel) throws IOException {
        final int size = readInt(fileChannel);
        if (size < 0) {
            return null;
        } else {
            final byte[] b = new byte[size];
            fileChannel.read(ByteBuffer.wrap(b));
            return Utf8Utils.bytesToString(b);
        }
    }

    public static String readString(ByteBuffer buffer) throws IOException {
        final int size = readInt(buffer);
        if (size < 0) {
            return null;
        } else {
            final byte[] b = new byte[size];
            buffer.get(b);
            return Utf8Utils.bytesToString(b);
        }
    }

}
