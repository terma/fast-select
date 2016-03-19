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

import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@SuppressWarnings("WeakerAccess")
public class FastSelectByteTest {

    @Test
    public void shouldSelectAndSortByByteColumn() {
        FastSelect<TestIntByte> database = new FastSelectBuilder<>(TestIntByte.class).create();
        database.addAll(asList(
                new TestIntByte(1, (byte) 11),
                new TestIntByte(1, (byte) 4),
                new TestIntByte(1, (byte) 98)));

        List result = database.selectAndSort(
                new AbstractRequest[]{new IntRequest("value1", new int[]{1})}, "value2");

        Assert.assertEquals(asList(
                new TestIntByte(1, (byte) 4),
                new TestIntByte(1, (byte) 11),
                new TestIntByte(1, (byte) 98)),
                result);
    }

    @Test
    public void shouldSelectAndSortByShortColumn() {
        FastSelect<TestLongShort> database = new FastSelectBuilder<>(TestLongShort.class).create();
        database.addAll(asList(
                new TestLongShort(1, (short) 11),
                new TestLongShort(1, (short) 4),
                new TestLongShort(1, (short) 98)));

        List result = database.selectAndSort(
                new AbstractRequest[]{new LongRequest("long1", new long[]{1})}, "short1");

        Assert.assertEquals(asList(
                new TestLongShort(1, (short) 4),
                new TestLongShort(1, (short) 11),
                new TestLongShort(1, (short) 98)),
                result);
    }

    @Test
    public void shouldSelectIfManyBlocksOneLevel() {
        FastSelect<TestIntByte> database = new FastSelectBuilder<>(TestIntByte.class).blockSize(1).create();
        database.addAll(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 0),
                new TestIntByte(1000, (byte) 0)));

        List result = database.select(new AbstractRequest[]{new IntRequest("value1", new int[]{12})});

        Assert.assertEquals(singletonList(new TestIntByte(12, (byte) 0)), result);
    }

    @Test
    public void shouldSupportAddMultipleTimes() {
        FastSelect<TestIntByte> database = new FastSelectBuilder<>(TestIntByte.class).blockSize(1).create();
        database.addAll(singletonList(new TestIntByte(12, (byte) 0)));
        database.addAll(singletonList(new TestIntByte(9, (byte) 0)));
        database.addAll(singletonList(new TestIntByte(1000, (byte) 0)));

        List result = database.select(new AbstractRequest[]{new IntRequest("value1", new int[]{12})});

        Assert.assertEquals(singletonList(new TestIntByte(12, (byte) 0)), result);
    }

    @Test
    public void shouldSelectByZero() {
        FastSelect<TestIntByte> database = new FastSelectBuilder<>(TestIntByte.class).blockSize(1).create();
        database.addAll(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 91),
                new TestIntByte(1000, (byte) 89)));

        List result = database.select(new AbstractRequest[]{
                new ByteRequest("value2", new int[]{0})
        });

        Assert.assertEquals(singletonList(new TestIntByte(12, (byte) 0)), result);
    }

    @Test
    public void shouldCorrectlyRestoreByteField() {
        FastSelect<TestIntByte> database = new FastSelectBuilder<>(TestIntByte.class).create();
        database.addAll(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(12, (byte) -1),
                new TestIntByte(12, (byte) 1),
                new TestIntByte(12, Byte.MAX_VALUE),
                new TestIntByte(12, Byte.MIN_VALUE)));

        List result = database.select(new AbstractRequest[]{new IntRequest("value1", new int[]{12})});

        Assert.assertEquals(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(12, (byte) -1),
                new TestIntByte(12, (byte) 1),
                new TestIntByte(12, Byte.MAX_VALUE),
                new TestIntByte(12, Byte.MIN_VALUE)),
                result);
    }

    public static class TestIntByte {
        public int value1;
        public byte value2;

        // empty constructor for database to be able restore object
        @SuppressWarnings("unused")
        public TestIntByte() {
            this(0, (byte) 0);
        }

        TestIntByte(int value, byte value2) {
            this.value1 = value;
            this.value2 = value2;
        }

        @Override
        public String toString() {
            return "TestIntByte{" +
                    "value1=" + value1 +
                    ", value2=" + value2 +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestIntByte that = (TestIntByte) o;
            return value1 == that.value1 &&
                    value2 == that.value2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value1, value2);
        }
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
