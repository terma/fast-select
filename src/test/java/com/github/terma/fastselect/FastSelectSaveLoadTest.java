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

package com.github.terma.fastselect;

import com.github.terma.fastselect.data.*;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;

import static com.github.terma.fastselect.utils.IOUtils.*;

@SuppressWarnings("WeakerAccess")
public class FastSelectSaveLoadTest {

    @Test
    public void loadFromEmpty() throws IOException {
        FastSelect<TestDoubleLongShort> fastSelect = new FastSelectBuilder<>(TestDoubleLongShort.class).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        fc.position(0);
        fastSelect.load(fc, 1);
        fc.close();

        Assert.assertEquals(0, fastSelect.size());
    }

    @Test
    public void loadWhenZeroItems() throws IOException {
        FastSelect<TestDoubleLongShort> fastSelect = new FastSelectBuilder<>(TestDoubleLongShort.class).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        writeInt(fc, Data.STORAGE_FORMAT_VERSION);
        writeInt(fc, 0);
        writeInt(fc, 0);
        fc.position(0);
        fastSelect.load(fc, 1);
        fc.close();

        Assert.assertEquals(0, fastSelect.size());
    }

    @Test
    public void loadWhenZeroItemsButColumnsPresent() throws IOException {
        FastSelect<TestDoubleLongShort> fastSelect = new FastSelectBuilder<>(TestDoubleLongShort.class).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        int headerEnd = 4 + 4 + 4 + getStringBytesSize(LongData.class.getName()) + getStringBytesSize("long1")
                + getStringBytesSize(ShortData.class.getName()) + getStringBytesSize("short1");

        writeInt(fc, Data.STORAGE_FORMAT_VERSION);
        writeInt(fc, 0); // records
        writeInt(fc, 2); // columns
        writeString(fc, LongData.class.getName());
        writeString(fc, "long1");
        writeLong(fc, headerEnd);
        writeInt(fc, 0);
        writeString(fc, ShortData.class.getName());
        writeString(fc, "short1");
        writeLong(fc, headerEnd);
        writeInt(fc, 0);

        fc.position(0);
        fastSelect.load(fc, 1);
        fc.close();

        Assert.assertEquals(0, fastSelect.size());
    }

    @Test
    public void loadOfUnexpectedFormatVersionThrowException() throws IOException {
        FastSelect<TestDoubleLongShort> fastSelect = new FastSelectBuilder<>(TestDoubleLongShort.class).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        fc.write((ByteBuffer) ByteBuffer.allocate(Data.INT_BYTES).putInt(-1).flip());
        fc.write((ByteBuffer) ByteBuffer.allocate(Data.INT_BYTES).putInt(0).flip());
        fc.position(0);
        try {
            fastSelect.load(fc, 1);
            Assert.fail("where is my exception?");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Unsupported format version: -1, expected: 1", e.getMessage());
        }
    }

    @Test
    public void loadWhenFormatVersionIsZeroThrowExceptionThatFileCorrupted() throws IOException {
        FastSelect<TestDoubleLongShort> fastSelect = new FastSelectBuilder<>(TestDoubleLongShort.class).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        fc.write((ByteBuffer) ByteBuffer.allocate(Data.INT_BYTES).putInt(0).flip());
        fc.write((ByteBuffer) ByteBuffer.allocate(Data.INT_BYTES).putInt(0).flip());
        fc.position(0);
        try {
            fastSelect.load(fc, 1);
            Assert.fail("where is my exception?");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Corrupted data! Ensure that you create dump properly.", e.getMessage());
        }
    }

    @Test
    public void load() throws IOException {
        FastSelect<TestDoubleLongShort> fastSelect = new FastSelectBuilder<>(TestDoubleLongShort.class).blockSize(1).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        int headerEnd = 4 + 4 + 4
                + getStringBytesSize(DoubleData.class.getName()) + getStringBytesSize("doubleValue") + 8 + 4
                + getStringBytesSize(LongData.class.getName()) + getStringBytesSize("long1") + 8 + 4
                + getStringBytesSize(ShortData.class.getName()) + getStringBytesSize("short1") + 8 + 4;

        writeInt(fc, Data.STORAGE_FORMAT_VERSION);
        writeInt(fc, 2); // records
        writeInt(fc, 3); // columns
        writeString(fc, DoubleData.class.getName());
        writeString(fc, "doubleValue");
        writeLong(fc, headerEnd);
        writeInt(fc, Data.DOUBLE_BYTES * 2);
        writeString(fc, LongData.class.getName());
        writeString(fc, "long1");
        writeLong(fc, headerEnd + Data.DOUBLE_BYTES * 2);
        writeInt(fc, Data.LONG_BYTES * 2);
        writeString(fc, ShortData.class.getName());
        writeString(fc, "short1");
        writeLong(fc, headerEnd + DoubleData.DOUBLE_BYTES * 2 + Data.LONG_BYTES * 2);
        writeInt(fc, Data.SHORT_BYTES * 2);

        fc.write((ByteBuffer) ByteBuffer.allocate(1024).putDouble(Double.MAX_VALUE).putDouble(0).flip());
        fc.write((ByteBuffer) ByteBuffer.allocate(1024).putLong(Long.MAX_VALUE).putLong(0).flip());
        fc.write((ByteBuffer) ByteBuffer.allocate(1024).putShort(Short.MAX_VALUE).putShort(Short.MIN_VALUE).flip());

        fc.position(0);
        fastSelect.load(fc, 1);
        fc.close();

        Assert.assertEquals(2, fastSelect.size());
        Assert.assertEquals(
                Arrays.asList(
                        new TestDoubleLongShort(Double.MAX_VALUE, Long.MAX_VALUE, Short.MAX_VALUE),
                        new TestDoubleLongShort(0.0, 0, Short.MIN_VALUE)),
                fastSelect.select()
        );
    }

    @Test
    public void loadIfDumpHasMoreColumnsThanDataClass() throws IOException {
        FastSelect<TestAllTypes> fastSelect =
                new FastSelectBuilder<>(TestAllTypes.class).inc(1).blockSize(1).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        int headerEnd = 4 + 4 + 4
                + getStringBytesSize(LongData.class.getName()) + getStringBytesSize("longValue") + 8 + 4 +
                +getStringBytesSize(LongData.class.getName()) + getStringBytesSize("nonExLongValue") + 8 + 4;

        writeInt(fc, Data.STORAGE_FORMAT_VERSION);
        writeInt(fc, 2); // records
        writeInt(fc, 2); // columns
        writeString(fc, LongData.class.getName());
        writeString(fc, "longValue");
        writeLong(fc, headerEnd);
        writeInt(fc, Data.LONG_BYTES * 2);
        writeString(fc, LongData.class.getName());
        writeString(fc, "nonExLongValue");
        writeLong(fc, headerEnd + Data.LONG_BYTES * 2);
        writeInt(fc, Data.LONG_BYTES * 2);

        fc.write((ByteBuffer) ByteBuffer.allocate(1024).putLong(Long.MAX_VALUE).putLong(0).flip());
        fc.write((ByteBuffer) ByteBuffer.allocate(1024).putLong(Long.MIN_VALUE).putLong(-1).flip());

        fc.position(0);
        fastSelect.load(fc, 1);
        fc.close();

        Assert.assertEquals(2, fastSelect.size());
        Assert.assertEquals(
                Arrays.asList(
                        new TestAllTypes().andLongValue(Long.MAX_VALUE),
                        new TestAllTypes().andLongValue(0)),
                fastSelect.select()
        );
    }

    @Test
    public void loadIfOneOfColumnNotPresentInDump() throws IOException {
        FastSelect<TestAllTypes> fastSelect =
                new FastSelectBuilder<>(TestAllTypes.class).inc(1).blockSize(1).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        int headerEnd = 4 + 4 + 4
                + getStringBytesSize(LongData.class.getName()) + getStringBytesSize("longValue") + 8 + 4;

        writeInt(fc, Data.STORAGE_FORMAT_VERSION);
        writeInt(fc, 2); // records
        writeInt(fc, 1); // columns
        writeString(fc, LongData.class.getName());
        writeString(fc, "longValue");
        writeLong(fc, headerEnd);
        writeInt(fc, Data.LONG_BYTES * 2);

        fc.write((ByteBuffer) ByteBuffer.allocate(1024).putLong(Long.MAX_VALUE).putLong(0).flip());

        fc.position(0);
        fastSelect.load(fc, 1);
        fc.close();

        Assert.assertEquals(2, fastSelect.size());
        Assert.assertEquals(
                Arrays.asList(
                        new TestAllTypes().andLongValue(Long.MAX_VALUE),
                        new TestAllTypes().andLongValue(0)),
                fastSelect.select()
        );
    }

    @Test
    public void saveAndLoadDoubleLongShort() throws IOException {
        FastSelect<TestDoubleLongShort> fastSelect = new FastSelectBuilder<>(TestDoubleLongShort.class).blockSize(1).create();
        fastSelect.addAll(Arrays.asList(
                new TestDoubleLongShort(1.23, 0, (short) 0),
                new TestDoubleLongShort(Double.MAX_VALUE, Long.MAX_VALUE, Short.MIN_VALUE)));

        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        fastSelect.save(fc);

        fc.position(0);
        FastSelect<TestDoubleLongShort> fastSelect1 = new FastSelectBuilder<>(TestDoubleLongShort.class).blockSize(1).create();
        fastSelect1.load(fc, 1);

        fc.close();
        //noinspection ResultOfMethodCallIgnored
        f.delete();

        Assert.assertEquals(2, fastSelect1.size());
        Assert.assertEquals(
                Arrays.asList(
                        new TestDoubleLongShort(1.23, 0, (short) 0),
                        new TestDoubleLongShort(Double.MAX_VALUE, Long.MAX_VALUE, Short.MIN_VALUE)),
                fastSelect1.select()
        );
    }

    @Test
    public void parallelLoad() throws IOException {
        FastSelect<TestDoubleLongShort> fastSelect = new FastSelectBuilder<>(TestDoubleLongShort.class).blockSize(1).create();
        fastSelect.addAll(Arrays.asList(
                new TestDoubleLongShort(1.1, 0, (short) 0),
                new TestDoubleLongShort(1.1, Long.MAX_VALUE, Short.MIN_VALUE)));

        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        fastSelect.save(fc);

        fc.position(0);
        FastSelect<TestDoubleLongShort> fastSelect1 = new FastSelectBuilder<>(TestDoubleLongShort.class).blockSize(1).create();
        fastSelect1.load(fc, 5);

        fc.close();
        //noinspection ResultOfMethodCallIgnored
        f.delete();

        Assert.assertEquals(2, fastSelect1.size());
        Assert.assertEquals(
                Arrays.asList(
                        new TestDoubleLongShort(1.1, 0, (short) 0),
                        new TestDoubleLongShort(1.1, Long.MAX_VALUE, Short.MIN_VALUE)),
                fastSelect1.select()
        );
    }

    @Test
    public void saveAndLoadIntByte() throws IOException {
        FastSelect<TestIntByte> fastSelect = new FastSelectBuilder<>(TestIntByte.class).blockSize(1).create();
        fastSelect.addAll(Arrays.asList(new TestIntByte(0, (byte) 0), new TestIntByte(Integer.MAX_VALUE, Byte.MIN_VALUE)));

        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        fastSelect.save(fc);

        fc.position(0);
        FastSelect<TestIntByte> fastSelect1 = new FastSelectBuilder<>(TestIntByte.class).blockSize(1).create();
        fastSelect1.load(fc, 1);

        fc.close();
        //noinspection ResultOfMethodCallIgnored
        f.delete();

        Assert.assertEquals(2, fastSelect1.size());
        Assert.assertEquals(
                Arrays.asList(
                        new TestIntByte(0, (byte) 0),
                        new TestIntByte(Integer.MAX_VALUE, Byte.MIN_VALUE)),
                fastSelect1.select()
        );
    }

    @Test
    public void saveAndLoadMultiX() throws IOException {
        FastSelect<TestAllTypes> fastSelect = new FastSelectBuilder<>(TestAllTypes.class).blockSize(1).create();
        fastSelect.addAll(Arrays.asList(
                new TestAllTypes()
                        .andMultiByte(new byte[]{Byte.MIN_VALUE, -1, Byte.MAX_VALUE})
                        .andMultiShort(new short[]{Short.MIN_VALUE, -1, Short.MAX_VALUE})
                        .andMultiInt(new int[]{Integer.MIN_VALUE, -1, Integer.MAX_VALUE})
                        .andMultiLong(new long[]{Long.MIN_VALUE, -1, Long.MAX_VALUE}),
                new TestAllTypes()));

        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        fastSelect.save(fc);

        fc.position(0);
        FastSelect<TestAllTypes> fastSelect1 = new FastSelectBuilder<>(TestAllTypes.class).blockSize(1).create();
        fastSelect1.load(fc, 1);

        fc.close();
        //noinspection ResultOfMethodCallIgnored
        f.delete();

        Assert.assertEquals(2, fastSelect1.size());
        Assert.assertEquals(
                Arrays.asList(
                        new TestAllTypes()
                                .andMultiByte(new byte[]{Byte.MIN_VALUE, -1, Byte.MAX_VALUE})
                                .andMultiShort(new short[]{Short.MIN_VALUE, -1, Short.MAX_VALUE})
                                .andMultiInt(new int[]{Integer.MIN_VALUE, -1, Integer.MAX_VALUE})
                                .andMultiLong(new long[]{Long.MIN_VALUE, -1, Long.MAX_VALUE}),
                        new TestAllTypes()),
                fastSelect1.select()
        );
    }

    @Test
    public void saveAndLoadCompressedString() throws IOException {
        FastSelect<TestCompressedString> fastSelect = new FastSelectBuilder<>(TestCompressedString.class).blockSize(1).create();
        fastSelect.addAll(Arrays.asList(new TestCompressedString(null, null, null),
                new TestCompressedString("A", "BB", "ZZZ")));

        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        fastSelect.save(fc);

        fc.position(0);
        FastSelect<TestCompressedString> fastSelect1 = new FastSelectBuilder<>(TestCompressedString.class).blockSize(1).create();
        fastSelect1.load(fc, 1);

        fc.close();
        //noinspection ResultOfMethodCallIgnored
        f.delete();

        Assert.assertEquals(2, fastSelect1.size());
        Assert.assertEquals(
                Arrays.asList(
                        new TestCompressedString(null, null, null),
                        new TestCompressedString("A", "BB", "ZZZ")),
                fastSelect1.select()
        );
    }

    @Test
    public void selectCompressedStringAfterSaveLoad() throws IOException {
        FastSelect<TestCompressedString> fastSelect = new FastSelectBuilder<>(TestCompressedString.class).blockSize(1).create();
        fastSelect.addAll(Arrays.asList(new TestCompressedString(null, null, null),
                new TestCompressedString("A", "BB", "ZZZ")));

        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        fastSelect.save(fc);

        fc.position(0);
        FastSelect<TestCompressedString> fastSelect1 = new FastSelectBuilder<>(TestCompressedString.class).blockSize(1).create();
        fastSelect1.load(fc, 1);

        fc.close();
        //noinspection ResultOfMethodCallIgnored
        f.delete();

        Assert.assertEquals(
                Collections.singletonList(new TestCompressedString("A", "BB", "ZZZ")),
                fastSelect1.select(new StringCompressedByteNoCaseLikeRequest("string1", "a"))
        );

        Assert.assertEquals(
                Collections.singletonList(new TestCompressedString("A", "BB", "ZZZ")),
                fastSelect1.select(new StringCompressedShortNoCaseLikeRequest("string2", "b"))
        );

        Assert.assertEquals(
                Collections.singletonList(new TestCompressedString("A", "BB", "ZZZ")),
                fastSelect1.select(new StringCompressedIntNoCaseLikeRequest("string3", "z"))
        );
    }

    @Test
    public void selectStringAfterSaveLoad() throws IOException {
        FastSelect<TestString> fastSelect = new FastSelectBuilder<>(TestString.class).blockSize(1).create();
        fastSelect.addAll(Arrays.asList(new TestString(null), new TestString("A")));

        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        fastSelect.save(fc);

        fc.position(0);
        FastSelect<TestString> fastSelect1 = new FastSelectBuilder<>(TestString.class).blockSize(1).create();
        fastSelect1.load(fc, 1);

        fc.close();
        //noinspection ResultOfMethodCallIgnored
        f.delete();

        Assert.assertEquals(
                Collections.singletonList(new TestString("A")),
                fastSelect1.select(new StringNoCaseLikeRequest("string1", "a"))
        );
    }

    @Test
    public void selectMultiByteAfterSaveLoad() throws IOException {
        FastSelect<TestMultiByte> fastSelect = new FastSelectBuilder<>(TestMultiByte.class).blockSize(1).create();
        fastSelect.addAll(Arrays.asList(
                new TestMultiByte(new byte[]{Byte.MAX_VALUE, Byte.MIN_VALUE}),
                new TestMultiByte(new byte[]{}),
                new TestMultiByte(new byte[]{0})));

        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        fastSelect.save(fc);

        fc.position(0);
        FastSelect<TestMultiByte> fastSelect1 = new FastSelectBuilder<>(TestMultiByte.class).blockSize(1).create();
        fastSelect1.load(fc, 1);

        fc.close();
        //noinspection ResultOfMethodCallIgnored
        f.delete();

        Assert.assertEquals(
                Collections.singletonList(new TestMultiByte(new byte[]{Byte.MAX_VALUE, Byte.MIN_VALUE})),
                fastSelect1.select(new MultiByteRequest("multiByte", Byte.MAX_VALUE))
        );
    }

    public static class TestDoubleLongShort {
        public double doubleValue;
        public long long1;
        public short short1;

        // empty constructor for database to be able restore object
        @SuppressWarnings("unused")
        public TestDoubleLongShort() {
            this(0, 0, (short) 0);
        }

        TestDoubleLongShort(double doubleValue, long long1, short short1) {
            this.long1 = long1;
            this.short1 = short1;
            this.doubleValue = doubleValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestDoubleLongShort that = (TestDoubleLongShort) o;

            if (Double.compare(that.doubleValue, doubleValue) != 0) return false;
            if (long1 != that.long1) return false;
            return short1 == that.short1;

        }

        @Override
        public String toString() {
            return "TestDoubleLongShort{" + "doubleValue=" + doubleValue +
                    ", long1=" + long1 + ", short1=" + short1 + '}';
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(doubleValue);
            result = (int) (temp ^ (temp >>> 32));
            result = 31 * result + (int) (long1 ^ (long1 >>> 32));
            result = 31 * result + (int) short1;
            return result;
        }

    }

    public static class TestCompressedString {
        @StringCompressedByte
        public String string1;
        @StringCompressedShort
        public String string2;
        @StringCompressedInt
        public String string3;

        @SuppressWarnings("unused")
        public TestCompressedString() {
        }

        public TestCompressedString(String string1, String string2, String string3) {
            this.string1 = string1;
            this.string2 = string2;
            this.string3 = string3;
        }

        @Override
        public String toString() {
            return "TestCompressedString{" +
                    "string1='" + string1 + '\'' +
                    ", string2='" + string2 + '\'' +
                    ", string3='" + string3 + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestCompressedString that = (TestCompressedString) o;

            if (string1 != null ? !string1.equals(that.string1) : that.string1 != null) return false;
            if (string2 != null ? !string2.equals(that.string2) : that.string2 != null) return false;
            return string3 != null ? string3.equals(that.string3) : that.string3 == null;

        }

        @Override
        public int hashCode() {
            int result = string1 != null ? string1.hashCode() : 0;
            result = 31 * result + (string2 != null ? string2.hashCode() : 0);
            result = 31 * result + (string3 != null ? string3.hashCode() : 0);
            return result;
        }
    }

    public static class TestMultiByte {
        public byte[] multiByte;

        public TestMultiByte() {
        }

        public TestMultiByte(byte[] multiByte) {
            this.multiByte = multiByte;
        }

        @Override
        public String toString() {
            return "TestMultiByte{" + "multiByte=" + Arrays.toString(multiByte) + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestMultiByte that = (TestMultiByte) o;

            return Arrays.equals(multiByte, that.multiByte);

        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(multiByte);
        }


    }

    @SuppressWarnings("unused")
    public static class TestString {
        public String string1;

        public TestString() {
        }

        public TestString(String string1) {
            this.string1 = string1;
        }

        @Override
        public String toString() {
            return "TestString{" + "string1='" + string1 + '\'' + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestString that = (TestString) o;

            return string1 != null ? string1.equals(that.string1) : that.string1 == null;

        }

        @Override
        public int hashCode() {
            return string1 != null ? string1.hashCode() : 0;
        }
    }

    public static class TestIntByte {
        public int int1;
        public byte byte1;

        // empty constructor for database to be able restore object
        @SuppressWarnings("unused")
        public TestIntByte() {
            this(0, (byte) 0);
        }

        TestIntByte(int int1, byte byte1) {
            this.int1 = int1;
            this.byte1 = byte1;
        }

        @Override
        public String toString() {
            return "TestIntByte{" + "int1=" + int1 + ", byte1=" + byte1 + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestIntByte that = (TestIntByte) o;
            return int1 == that.int1 && byte1 == that.byte1;
        }

        @Override
        public int hashCode() {
            int result = int1;
            result = 31 * result + (int) byte1;
            return result;
        }

    }

}
