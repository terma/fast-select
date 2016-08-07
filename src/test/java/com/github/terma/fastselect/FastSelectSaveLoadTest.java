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

import com.github.terma.fastselect.data.Data;
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

@SuppressWarnings("WeakerAccess")
public class FastSelectSaveLoadTest {

    @Test
    public void loadFromEmpty() throws IOException {
        FastSelect<TestLongShort> fastSelect = new FastSelectBuilder<>(TestLongShort.class).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        fc.write((ByteBuffer) ByteBuffer.allocate((int) Data.INT_BYTES).putInt(Data.STORAGE_FORMAT_VERSION).flip());
        fc.write((ByteBuffer) ByteBuffer.allocate((int) Data.INT_BYTES).putInt(0).flip());
        fc.position(0);
        fastSelect.load(fc);
        fc.close();

        Assert.assertEquals(0, fastSelect.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void loadOfUnexpectedFormatVersionThrowException() throws IOException {
        FastSelect<TestLongShort> fastSelect = new FastSelectBuilder<>(TestLongShort.class).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        fc.write((ByteBuffer) ByteBuffer.allocate((int) Data.INT_BYTES).putInt(-1).flip());
        fc.write((ByteBuffer) ByteBuffer.allocate((int) Data.INT_BYTES).putInt(0).flip());
        fc.position(0);
        fastSelect.load(fc);
    }

    @Test
    public void load() throws IOException {
        FastSelect<TestLongShort> fastSelect = new FastSelectBuilder<>(TestLongShort.class).blockSize(1).create();
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        fc.write((ByteBuffer) ByteBuffer.allocate((int) Data.INT_BYTES).putInt(Data.STORAGE_FORMAT_VERSION).flip());
        fc.write((ByteBuffer) ByteBuffer.allocate((int) Data.INT_BYTES).putInt(2).flip());

        fc.write((ByteBuffer) ByteBuffer.allocate(1024).putLong(Long.MAX_VALUE).putLong(0).flip());
        fc.write((ByteBuffer) ByteBuffer.allocate(1024).putShort(Short.MAX_VALUE).putShort(Short.MIN_VALUE).flip());

        fc.position(0);
        fastSelect.load(fc);
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
    public void saveAndLoad() throws IOException {
        FastSelect<TestLongShort> fastSelect = new FastSelectBuilder<>(TestLongShort.class).blockSize(1).create();
        fastSelect.addAll(Arrays.asList(new TestLongShort(0, (short) 0), new TestLongShort(Long.MAX_VALUE, Short.MIN_VALUE)));

        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        fastSelect.save(fc);

        fc.position(0);
        FastSelect<TestLongShort> fastSelect1 = new FastSelectBuilder<>(TestLongShort.class).blockSize(1).create();
        fastSelect1.load(fc);

        fc.close();
        f.delete();

        Assert.assertEquals(2, fastSelect1.size());
        Assert.assertEquals(
                Arrays.asList(
                        new TestLongShort(0, (short) 0),
                        new TestLongShort(Long.MAX_VALUE, Short.MIN_VALUE)),
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

}
