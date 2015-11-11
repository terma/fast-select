/*
Copyright 2015 Artem Stasiuk

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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ArrayLayoutFastSelectTest {

    @Test
    public void shouldSelectEmptyResultIfNoData() {
        List result = new ArrayLayoutFastSelect<>(10, TestIntByte.class, Arrays.asList(
                new ArrayLayoutFastSelect.Column("value1", int.class),
                new ArrayLayoutFastSelect.Column("value2", byte.class)
        ))
                .select(new MultiRequest[]{new MultiRequest("value1", new int[]{34})});
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldSelectIfPresentByOneField() {
        ArrayLayoutFastSelect<TestIntByte> database = new ArrayLayoutFastSelect<>(10, TestIntByte.class,
                Arrays.asList(
                        new ArrayLayoutFastSelect.Column("value1", int.class),
                        new ArrayLayoutFastSelect.Column("value2", byte.class)
                ));
        database.addAll(Arrays.asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 0),
                new TestIntByte(1000, (byte) 0)));

        List result = database.select(new MultiRequest[]{new MultiRequest("value1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestIntByte(12, (byte) 0)), result);
    }

    @Test
    public void shouldSelectIfTwoBlocks() {
        ArrayLayoutFastSelect<TestIntByte> database = new ArrayLayoutFastSelect<>(1, TestIntByte.class,
                Arrays.asList(
                        new ArrayLayoutFastSelect.Column("value1", int.class),
                        new ArrayLayoutFastSelect.Column("value2", byte.class)
                ));
        database.addAll(Arrays.asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 0),
                new TestIntByte(1000, (byte) 0)));

        List result = database.select(new MultiRequest[]{new MultiRequest("value1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestIntByte(12, (byte) 0)), result);
    }

    @Test
    public void shouldSelectByLongField() {
        ArrayLayoutFastSelect<TestLongShort> database = new ArrayLayoutFastSelect<>(1, TestLongShort.class,
                Collections.singletonList(new ArrayLayoutFastSelect.Column("long1", long.class)));
        database.addAll(Arrays.asList(
                new TestLongShort(12L, (short) 0),
                new TestLongShort(9, (short) 0),
                new TestLongShort(1000, (short) 0)));

        List result = database.select(new MultiRequest[]{new MultiRequest("long1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestLongShort(12, (short) 0)), result);
    }

    @Test
    public void shouldSelectByShortField() {
        ArrayLayoutFastSelect<TestLongShort> database = new ArrayLayoutFastSelect<>(1, TestLongShort.class,
                Arrays.asList(
                        new ArrayLayoutFastSelect.Column("long1", long.class),
                        new ArrayLayoutFastSelect.Column("short1", short.class)
                ));
        database.addAll(Arrays.asList(
                new TestLongShort(12L, (short) 5),
                new TestLongShort(9, (short) 3),
                new TestLongShort(1000, (short) 0)));

        List result = database.select(new MultiRequest[]{new MultiRequest("short1", new int[]{0})});

        Assert.assertEquals(Collections.singletonList(new TestLongShort(1000, (short) 0)), result);
    }

    @Test
    public void shouldProvideSize() {
        ArrayLayoutFastSelect<TestIntByte> database = new ArrayLayoutFastSelect<>(1, TestIntByte.class,
                Arrays.asList(
                        new ArrayLayoutFastSelect.Column("value1", int.class),
                        new ArrayLayoutFastSelect.Column("value2", byte.class)
                ));
        database.addAll(Arrays.asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 0),
                new TestIntByte(1000, (byte) 0)));

        Assert.assertEquals(3, database.size());
    }

    static class TestIntByte {
        public int value1;
        public byte value2;

        @SuppressWarnings("unused")
            // empty constructor for database to be able restore object
        TestIntByte() {
            this(0, (byte) 0);
        }

        TestIntByte(int value, byte value2) {
            this.value1 = value;
            this.value2 = value2;
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

    static class TestLongShort {
        public long long1;
        public short short1;

        @SuppressWarnings("unused")
            // empty constructor for database to be able restore object
        TestLongShort() {
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
            return "TestLongShort{" +
                    "long1=" + long1 +
                    ", short1=" + short1 +
                    '}';
        }

        @Override
        public int hashCode() {
            return Objects.hash(long1, short1);
        }

    }

}
