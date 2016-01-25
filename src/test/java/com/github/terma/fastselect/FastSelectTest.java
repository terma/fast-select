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

import com.github.terma.fastselect.data.IntStringData;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;

public class FastSelectTest {

    @Test
    public void shouldSelectEmptyResultIfNoData() {
        List result = new FastSelect<>(10, TestIntByte.class)
                .select(new AbstractRequest[]{new IntRequest("value1", new int[]{34})});
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldSelectIfPresentByOneField() {
        FastSelect<TestIntByte> database = new FastSelect<>(10, TestIntByte.class);
        database.addAll(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 0),
                new TestIntByte(1000, (byte) 0)));

        List result = database.select(new AbstractRequest[]{new Request("value1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestIntByte(12, (byte) 0)), result);
    }

    @Test
    public void shouldSelectAndSortByIntColumn() {
        FastSelect<TestIntByte> database = new FastSelect<>(TestIntByte.class);
        database.addAll(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 0),
                new TestIntByte(1000, (byte) 0)));

        List result = database.selectAndSort(
                new AbstractRequest[]{new ByteRequest("value2", new int[]{0})}, "value1");

        Assert.assertEquals(asList(
                new TestIntByte(9, (byte) 0),
                new TestIntByte(12, (byte) 0),
                new TestIntByte(1000, (byte) 0)),
                result);
    }

    @Test
    public void shouldSelectAndSortByByteColumn() {
        FastSelect<TestIntByte> database = new FastSelect<>(TestIntByte.class);
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
        FastSelect<TestLongShort> database = new FastSelect<>(TestLongShort.class);
        database.addAll(asList(
                new TestLongShort(1, (short) 11),
                new TestLongShort(1, (short) 4),
                new TestLongShort(1, (short) 98)));

        List result = database.selectAndSort(
                new AbstractRequest[]{new Request("long1", new int[]{1})}, "short1");

        Assert.assertEquals(asList(
                new TestLongShort(1, (short) 4),
                new TestLongShort(1, (short) 11),
                new TestLongShort(1, (short) 98)),
                result);
    }

    @Test
    public void shouldSelectAndSortByLongColumn() {
        FastSelect<TestLongShort> database = new FastSelect<>(TestLongShort.class);
        database.addAll(asList(
                new TestLongShort(Long.MAX_VALUE, (short) 1),
                new TestLongShort(9, (short) 1),
                new TestLongShort(1231312, (short) 1)));

        List result = database.selectAndSort(
                new AbstractRequest[]{new ShortRequest("short1", new int[]{1})}, "long1");

        Assert.assertEquals(asList(
                new TestLongShort(9, (short) 1),
                new TestLongShort(1231312, (short) 1),
                new TestLongShort(Long.MAX_VALUE, (short) 1)),
                result);
    }

    @Test
    public void supportStringField() {
        FastSelect<IntStringData> database = new FastSelect<>(10, IntStringData.class);
        database.addAll(asList(
                new IntStringData(1, ""),
                new IntStringData(1, "1"),
                new IntStringData(1, "abra")));

        List result = database.select(new AbstractRequest[]{new Request("value1", new int[]{1})});

        Assert.assertEquals(asList(
                new IntStringData(1, ""),
                new IntStringData(1, "1"),
                new IntStringData(1, "abra")
        ), result);
    }

    @Test
    public void selectByStringField() {
        FastSelect<IntStringData> database = new FastSelect<>(10, IntStringData.class);
        database.addAll(asList(
                new IntStringData(1, ""),
                new IntStringData(1, "1"),
                new IntStringData(1, "abra")));

        List result = database.select(new AbstractRequest[]{new StringRequest("value2", "1")});

        Assert.assertEquals(Collections.singletonList(new IntStringData(1, "1")), result);
    }

    @Test
    // todo return null if incoming string was null instead of empty
    public void supportNullableStringFieldAsEmpty() {
        FastSelect<IntStringData> database = new FastSelect<>(10, IntStringData.class);
        database.addAll(asList(
                new IntStringData(1, null),
                new IntStringData(1, "1"),
                new IntStringData(1, "abra")));

        List result = database.select(new Request[]{new Request("value1", new int[]{1})});

        Assert.assertEquals(asList(
                new IntStringData(1, ""),
                new IntStringData(1, "1"),
                new IntStringData(1, "abra")
        ), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionIfRequestNonExistentColumn() {
        FastSelect<TestIntByte> database = new FastSelect<>(10, TestIntByte.class);
        database.addAll(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 0),
                new TestIntByte(1000, (byte) 0)));

        database.select(new Request[]{new Request("a", new int[]{12})});
    }

    @Test
    public void shouldSelectIfManyBlocksOneLevel() {
        FastSelect<TestIntByte> database = new FastSelect<>(1, TestIntByte.class);
        database.addAll(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 0),
                new TestIntByte(1000, (byte) 0)));

        List result = database.select(new Request[]{new Request("value1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestIntByte(12, (byte) 0)), result);
    }

    @Test
    public void shouldSelectIfManyBlocksAndLevels() {
        FastSelect<TestIntByte> database = new FastSelect<>(new int[]{1, 1}, TestIntByte.class);
        database.addAll(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 0),
                new TestIntByte(1000, (byte) 0)));

        List result = database.select(new Request[]{new Request("value1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestIntByte(12, (byte) 0)), result);
    }

    @Test
    public void shouldSelectByTwoConditions() {
        FastSelect<TestIntByte> database = new FastSelect<>(1, TestIntByte.class);
        database.addAll(asList(
                new TestIntByte(12, (byte) 90),
                new TestIntByte(9, (byte) 91),
                new TestIntByte(1000, (byte) 89)));

        List result = database.select(new Request[]{
                new Request("value1", new int[]{12}),
                new Request("value2", new int[]{90})
        });

        Assert.assertEquals(Collections.singletonList(new TestIntByte(12, (byte) 90)), result);
    }

    @Test
    public void shouldSelectByZero() {
        FastSelect<TestIntByte> database = new FastSelect<>(1, TestIntByte.class);
        database.addAll(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 91),
                new TestIntByte(1000, (byte) 89)));

        List result = database.select(new Request[]{
                new Request("value2", new int[]{0})
        });

        Assert.assertEquals(Collections.singletonList(new TestIntByte(12, (byte) 0)), result);
    }

    @Test
    public void shouldSelectByLongField() {
        FastSelect<TestLongShort> database = new FastSelect<>(1, TestLongShort.class);
        database.addAll(asList(
                new TestLongShort(12L, (short) 0),
                new TestLongShort(9, (short) 0),
                new TestLongShort(1000, (short) 0)));

        List result = database.select(new Request[]{new Request("long1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestLongShort(12, (short) 0)), result);
    }

    @Test
    public void shouldSelectByMaxIntField() {
        FastSelect<TestIntByte> database = new FastSelect<>(1, TestIntByte.class);
        database.addAll(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 0),
                new TestIntByte(Integer.MAX_VALUE, (byte) 0)));

        List result = database.select(new AbstractRequest[]{new IntRequest("value1", new int[]{Integer.MAX_VALUE})});

        Assert.assertEquals(Collections.singletonList(new TestIntByte(Integer.MAX_VALUE, (byte) 0)), result);
    }

    @Test
    public void shouldSelectByShortField() {
        FastSelect<TestLongShort> database = new FastSelect<>(1, TestLongShort.class);
        database.addAll(asList(
                new TestLongShort(12L, (short) 5),
                new TestLongShort(9, (short) 3),
                new TestLongShort(1000, (short) 1)));

        List result = database.select(new Request[]{new Request("short1", new int[]{1})});

        Assert.assertEquals(Collections.singletonList(new TestLongShort(1000, (short) 1)), result);
    }

    @Test
    public void shouldProvideSize() {
        FastSelect<TestIntByte> database = new FastSelect<>(1, TestIntByte.class);
        database.addAll(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(9, (byte) 0),
                new TestIntByte(1000, (byte) 0)));

        Assert.assertEquals(3, database.size());
    }

    @Test
    public void shouldProvideSizeForLong() {
        FastSelect<TestLong> database = new FastSelect<>(1, TestLong.class);
        database.addAll(asList(
                new TestLong(12),
                new TestLong(9)));

        Assert.assertEquals(2, database.size());
    }

    @Test
    public void supportsLongMultiValuesColumns() {
        FastSelect<LongMultiValues> database = new FastSelect<>(1, LongMultiValues.class);
        database.addAll(asList(
                new LongMultiValues(new long[]{1, 2}),
                new LongMultiValues(new long[]{11, 12})));

        List<LongMultiValues> r = database.select(new Request[]{new Request("a", new int[]{12})});

        Assert.assertEquals(r, Collections.singletonList(new LongMultiValues(new long[]{11, 12})));
    }

    @Test
    public void supportsLongMultiValuesColumnsManyResult() {
        FastSelect<LongMultiValues> database = new FastSelect<>(1, LongMultiValues.class);
        database.addAll(asList(
                new LongMultiValues(new long[]{1, 2}),
                new LongMultiValues(new long[]{11, 12}),
                new LongMultiValues(new long[]{33, 12}),
                new LongMultiValues(new long[]{1, 0})
        ));

        List<LongMultiValues> r = database.select(new Request[]{new Request("a", new int[]{12})});

        Assert.assertEquals(asList(new LongMultiValues(new long[]{11, 12}), new LongMultiValues(new long[]{33, 12})), r);
    }

    @Test
    public void supportsLongMultiValuesColumnsManyCriteria() {
        FastSelect<LongMultiValues> database = new FastSelect<>(1, LongMultiValues.class);
        database.addAll(asList(
                new LongMultiValues(new long[]{1, 2}),
                new LongMultiValues(new long[]{11, 12}),
                new LongMultiValues(new long[]{33, 12}),
                new LongMultiValues(new long[]{1, 0})
        ));

        List<LongMultiValues> r = database.select(new Request[]{new Request("a", new int[]{1, 12})});

        Assert.assertEquals(
                asList(
                        new LongMultiValues(new long[]{1, 2}),
                        new LongMultiValues(new long[]{11, 12}),
                        new LongMultiValues(new long[]{33, 12}),
                        new LongMultiValues(new long[]{1, 0})),
                r);
    }

    @Test
    public void supportsShortMultiValuesColumns() {
        FastSelect<ShortMultiValues> database = new FastSelect<>(1, ShortMultiValues.class);
        database.addAll(asList(
                new ShortMultiValues(new short[]{1, 2}),
                new ShortMultiValues(new short[]{11, 12})));

        List<ShortMultiValues> r = database.select(new Request[]{new Request("a", new int[]{12})});

        Assert.assertEquals(r, Collections.singletonList(new ShortMultiValues(new short[]{11, 12})));
    }

    @Test
    public void supportsShortMultiValuesColumnsManyResult() {
        FastSelect<ShortMultiValues> database = new FastSelect<>(1, ShortMultiValues.class);
        database.addAll(asList(
                new ShortMultiValues(new short[]{1, 2}),
                new ShortMultiValues(new short[]{11, 12}),
                new ShortMultiValues(new short[]{33, 12}),
                new ShortMultiValues(new short[]{1, 0})
        ));

        List<ShortMultiValues> r = database.select(new Request[]{new Request("a", new int[]{12})});

        Assert.assertEquals(asList(new ShortMultiValues(new short[]{11, 12}), new ShortMultiValues(new short[]{33, 12})), r);
    }

    @Test
    public void supportsShortMultiValuesColumnsManyCriteria() {
        FastSelect<ShortMultiValues> database = new FastSelect<>(1, ShortMultiValues.class);
        database.addAll(asList(
                new ShortMultiValues(new short[]{1, 2}),
                new ShortMultiValues(new short[]{11, 12}),
                new ShortMultiValues(new short[]{33, 12}),
                new ShortMultiValues(new short[]{1, 0})
        ));

        List<ShortMultiValues> r = database.select(new Request[]{new Request("a", new int[]{1, 12})});

        Assert.assertEquals(
                asList(
                        new ShortMultiValues(new short[]{1, 2}),
                        new ShortMultiValues(new short[]{11, 12}),
                        new ShortMultiValues(new short[]{33, 12}),
                        new ShortMultiValues(new short[]{1, 0})),
                r);
    }

    @Test
    public void supportsByteMultiValuesColumns() {
        FastSelect<ByteMultiValues> database = new FastSelect<>(1, ByteMultiValues.class);
        database.addAll(asList(
                new ByteMultiValues(new byte[]{1, 2}),
                new ByteMultiValues(new byte[]{11, 12})));

        List<ByteMultiValues> r = database.select(new Request[]{new Request("a", new int[]{12})});

        Assert.assertEquals(r, Collections.singletonList(new ByteMultiValues(new byte[]{11, 12})));
    }

    @Test
    public void supportsByteMultiValuesColumnsManyResult() {
        FastSelect<ByteMultiValues> database = new FastSelect<>(1, ByteMultiValues.class);
        database.addAll(asList(
                new ByteMultiValues(new byte[]{1, 2}),
                new ByteMultiValues(new byte[]{11, 12}),
                new ByteMultiValues(new byte[]{33, 12}),
                new ByteMultiValues(new byte[]{1, 0})
        ));

        List<ByteMultiValues> r = database.select(new Request[]{new Request("a", new int[]{12})});

        Assert.assertEquals(asList(new ByteMultiValues(new byte[]{11, 12}), new ByteMultiValues(new byte[]{33, 12})), r);
    }

    @Test
    public void supportsByteMultiValuesColumnsManyCriteria() {
        FastSelect<ByteMultiValues> database = new FastSelect<>(1, ByteMultiValues.class);
        database.addAll(asList(
                new ByteMultiValues(new byte[]{1, 2}),
                new ByteMultiValues(new byte[]{11, 12}),
                new ByteMultiValues(new byte[]{33, 12}),
                new ByteMultiValues(new byte[]{1, 0})
        ));

        List<ByteMultiValues> r = database.select(new Request[]{new Request("a", new int[]{1, 12})});

        Assert.assertEquals(
                asList(
                        new ByteMultiValues(new byte[]{1, 2}),
                        new ByteMultiValues(new byte[]{11, 12}),
                        new ByteMultiValues(new byte[]{33, 12}),
                        new ByteMultiValues(new byte[]{1, 0})),
                r);
    }

    @Test
    public void shouldCorrectlyRestoreByteField() {
        FastSelect<TestIntByte> database = new FastSelect<>(10, TestIntByte.class);
        database.addAll(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(12, (byte) -1),
                new TestIntByte(12, (byte) 1),
                new TestIntByte(12, Byte.MAX_VALUE),
                new TestIntByte(12, Byte.MIN_VALUE)));

        List result = database.select(new Request[]{new Request("value1", new int[]{12})});

        Assert.assertEquals(asList(
                new TestIntByte(12, (byte) 0),
                new TestIntByte(12, (byte) -1),
                new TestIntByte(12, (byte) 1),
                new TestIntByte(12, Byte.MAX_VALUE),
                new TestIntByte(12, Byte.MIN_VALUE)),
                result);
    }

    @Test
    public void shouldCorrectlyRestoreIntField() {
        FastSelect<TestIntByte> database = new FastSelect<>(10, TestIntByte.class);
        database.addAll(asList(
                new TestIntByte(0, (byte) 1),
                new TestIntByte(-1, (byte) 1),
                new TestIntByte(1, (byte) 1),
                new TestIntByte(Integer.MAX_VALUE, (byte) 1),
                new TestIntByte(Integer.MIN_VALUE, (byte) 1)));

        List result = database.select(new Request[]{new Request("value2", new int[]{1})});

        Assert.assertEquals(asList(
                new TestIntByte(0, (byte) 1),
                new TestIntByte(-1, (byte) 1),
                new TestIntByte(1, (byte) 1),
                new TestIntByte(Integer.MAX_VALUE, (byte) 1),
                new TestIntByte(Integer.MIN_VALUE, (byte) 1)),
                result);
    }

    @Test
    public void shouldCorrectlyRestoreLongField() {
        List<TestLongShort> data = asList(
                new TestLongShort(0, (short) 1),
                new TestLongShort(-1, (short) 1),
                new TestLongShort(1, (short) 1),
                new TestLongShort(Long.MAX_VALUE, (short) 1),
                new TestLongShort(Long.MIN_VALUE, (short) 1));

        FastSelect<TestLongShort> database = new FastSelect<>(10, TestLongShort.class);
        database.addAll(data);

        List result = database.select(new Request[]{new Request("short1", new int[]{1})});

        Assert.assertEquals(data, result);
    }

    public static class TestIntByte {
        public int value1;
        public byte value2;

        // empty constructor for database to be able restore object
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

    public static class TestLong {
        public long value1;

        TestLong(long value) {
            this.value1 = value;
        }
    }

    public static class LongMultiValues {

        public long[] a;

        @SuppressWarnings("unused")
        public LongMultiValues() {
        }

        LongMultiValues(long[] value) {
            this.a = value;
        }

        @Override
        public String toString() {
            return "LongMultiValues{" + "a=" + Arrays.toString(a) + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LongMultiValues that = (LongMultiValues) o;
            return Arrays.equals(a, that.a);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(a);
        }
    }

    public static class ShortMultiValues {

        public short[] a;

        @SuppressWarnings("unused")
        public ShortMultiValues() {
        }

        ShortMultiValues(short[] value) {
            this.a = value;
        }

        @Override
        public String toString() {
            return "ShortMultiValues{" + "a=" + Arrays.toString(a) + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ShortMultiValues that = (ShortMultiValues) o;
            return Arrays.equals(a, that.a);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(a);
        }
    }

    public static class ByteMultiValues {

        public byte[] a;

        @SuppressWarnings("unused")
        public ByteMultiValues() {
        }

        ByteMultiValues(byte[] value) {
            this.a = value;
        }

        @Override
        public String toString() {
            return "ByteMultiValues{" + "a=" + Arrays.toString(a) + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ByteMultiValues that = (ByteMultiValues) o;
            return Arrays.equals(a, that.a);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(a);
        }
    }

    public static class TestLongShort {
        public long long1;
        public short short1;

        // empty constructor for database to be able restore object
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
