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
import java.util.Objects;

import static com.github.terma.fastselect.utils.IOUtils.*;

@SuppressWarnings("WeakerAccess")
public class FastSelectSaveLoadTest {

    @Test
    public void loadFromEmpty() throws IOException {
        FastSelect<TestLongShort> fastSelect = new FastSelectBuilder<>(TestLongShort.class).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        fc.position(0);
        fastSelect.load(fc, 1);
        fc.close();

        Assert.assertEquals(0, fastSelect.size());
    }

    @Test
    public void loadWhenZeroItems() throws IOException {
        FastSelect<TestLongShort> fastSelect = new FastSelectBuilder<>(TestLongShort.class).create();
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
        FastSelect<TestLongShort> fastSelect = new FastSelectBuilder<>(TestLongShort.class).create();
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

    @Test(expected = IllegalArgumentException.class)
    public void loadOfUnexpectedFormatVersionThrowException() throws IOException {
        FastSelect<TestLongShort> fastSelect = new FastSelectBuilder<>(TestLongShort.class).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        fc.write((ByteBuffer) ByteBuffer.allocate(Data.INT_BYTES).putInt(-1).flip());
        fc.write((ByteBuffer) ByteBuffer.allocate(Data.INT_BYTES).putInt(0).flip());
        fc.position(0);
        fastSelect.load(fc, 1);
    }

    @Test
    public void load() throws IOException {
        FastSelect<TestLongShort> fastSelect = new FastSelectBuilder<>(TestLongShort.class).blockSize(1).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        int headerEnd = 4 + 4 + 4
                + getStringBytesSize(LongData.class.getName()) + getStringBytesSize("long1") + 8 + 4
                + getStringBytesSize(ShortData.class.getName()) + getStringBytesSize("short1") + 8 + 4;

        writeInt(fc, Data.STORAGE_FORMAT_VERSION);
        writeInt(fc, 2); // records
        writeInt(fc, 2); // columns
        writeString(fc, LongData.class.getName());
        writeString(fc, "long1");
        writeLong(fc, headerEnd);
        writeInt(fc, Data.LONG_BYTES * 2);
        writeString(fc, ShortData.class.getName());
        writeString(fc, "short1");
        writeLong(fc, headerEnd + Data.LONG_BYTES * 2);
        writeInt(fc, Data.SHORT_BYTES * 2);

        fc.write((ByteBuffer) ByteBuffer.allocate(1024).putLong(Long.MAX_VALUE).putLong(0).flip());
        fc.write((ByteBuffer) ByteBuffer.allocate(1024).putShort(Short.MAX_VALUE).putShort(Short.MIN_VALUE).flip());

        fc.position(0);
        fastSelect.load(fc, 1);
        fc.close();

        Assert.assertEquals(2, fastSelect.size());
        Assert.assertEquals(
                Arrays.asList(
                        new TestLongShort(Long.MAX_VALUE, Short.MAX_VALUE),
                        new TestLongShort(0, Short.MIN_VALUE)),
                fastSelect.select()
        );
    }

    @Test
    public void saveAndLoadLongShort() throws IOException {
        FastSelect<TestLongShort> fastSelect = new FastSelectBuilder<>(TestLongShort.class).blockSize(1).create();
        fastSelect.addAll(Arrays.asList(new TestLongShort(0, (short) 0), new TestLongShort(Long.MAX_VALUE, Short.MIN_VALUE)));

        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        fastSelect.save(fc);

        fc.position(0);
        FastSelect<TestLongShort> fastSelect1 = new FastSelectBuilder<>(TestLongShort.class).blockSize(1).create();
        fastSelect1.load(fc, 1);

        fc.close();
        //noinspection ResultOfMethodCallIgnored
        f.delete();

        Assert.assertEquals(2, fastSelect1.size());
        Assert.assertEquals(
                Arrays.asList(
                        new TestLongShort(0, (short) 0),
                        new TestLongShort(Long.MAX_VALUE, Short.MIN_VALUE)),
                fastSelect1.select()
        );
    }

    @Test
    public void parallelLoad() throws IOException {
        FastSelect<TestLongShort> fastSelect = new FastSelectBuilder<>(TestLongShort.class).blockSize(1).create();
        fastSelect.addAll(Arrays.asList(new TestLongShort(0, (short) 0), new TestLongShort(Long.MAX_VALUE, Short.MIN_VALUE)));

        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        fastSelect.save(fc);

        fc.position(0);
        FastSelect<TestLongShort> fastSelect1 = new FastSelectBuilder<>(TestLongShort.class).blockSize(1).create();
        fastSelect1.load(fc, 5);

        fc.close();
        //noinspection ResultOfMethodCallIgnored
        f.delete();

        Assert.assertEquals(2, fastSelect1.size());
        Assert.assertEquals(
                Arrays.asList(
                        new TestLongShort(0, (short) 0),
                        new TestLongShort(Long.MAX_VALUE, Short.MIN_VALUE)),
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

    public static class TestLongShort {
        public long long1;
        public short short1;

        // empty constructor for database to be able restore object
        @SuppressWarnings("unused")
        public TestLongShort() {
            this(0, (byte) 0);
        }

        TestLongShort(long long1, short short1) {
            this.long1 = long1;
            this.short1 = short1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestLongShort that = (TestLongShort) o;
            return long1 == that.long1 &&
                    short1 == that.short1;
        }

        @Override
        public String toString() {
            return "TestLongShort {long1: " + long1 + ", short1: " + short1 + '}';
        }

        @Override
        public int hashCode() {
            return Objects.hash(long1, short1);
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
