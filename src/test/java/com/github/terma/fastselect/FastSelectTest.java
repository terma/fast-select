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

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class FastSelectTest {

    @Test
    public void shouldSelectEmptyResultIfNoData() {
        List result = new FastSelect<>(10, TestIntByteData.class)
                .select(new Request[]{new Request("value1", new int[]{34})});
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldSelectIfPresentByOneField() {
        FastSelect<TestIntByteData> database = new FastSelect<>(10, TestIntByteData.class);
        database.addAll(asList(
                new TestIntByteData(12, (byte) 0),
                new TestIntByteData(9, (byte) 0),
                new TestIntByteData(1000, (byte) 0)));

        List result = database.select(new Request[]{new Request("value1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestIntByteData(12, (byte) 0)), result);
    }

    @Test
    public void supportStringField() {
        FastSelect<IntStringData> database = new FastSelect<>(10, IntStringData.class);
        database.addAll(asList(
                new IntStringData(1, ""),
                new IntStringData(1, "1"),
                new IntStringData(1, "abra")));

        List result = database.select(new Request[]{new Request("value1", new int[]{1})});

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
        FastSelect<TestIntByteData> database = new FastSelect<>(10, TestIntByteData.class);
        database.addAll(asList(
                new TestIntByteData(12, (byte) 0),
                new TestIntByteData(9, (byte) 0),
                new TestIntByteData(1000, (byte) 0)));

        database.select(new Request[]{new Request("a", new int[]{12})});
    }

    @Test
    public void shouldSelectIfManyBlocksOneLevel() {
        FastSelect<TestIntByteData> database = new FastSelect<>(1, TestIntByteData.class);
        database.addAll(asList(
                new TestIntByteData(12, (byte) 0),
                new TestIntByteData(9, (byte) 0),
                new TestIntByteData(1000, (byte) 0)));

        List result = database.select(new Request[]{new Request("value1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestIntByteData(12, (byte) 0)), result);
    }

    @Test
    public void shouldSelectIfManyBlocksAndLevels() {
        FastSelect<TestIntByteData> database = new FastSelect<>(new int[]{1, 1}, TestIntByteData.class);
        database.addAll(asList(
                new TestIntByteData(12, (byte) 0),
                new TestIntByteData(9, (byte) 0),
                new TestIntByteData(1000, (byte) 0)));

        List result = database.select(new Request[]{new Request("value1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestIntByteData(12, (byte) 0)), result);
    }

    @Test
    public void shouldSelectByTwoConditions() {
        FastSelect<TestIntByteData> database = new FastSelect<>(1, TestIntByteData.class);
        database.addAll(asList(
                new TestIntByteData(12, (byte) 90),
                new TestIntByteData(9, (byte) 91),
                new TestIntByteData(1000, (byte) 89)));

        List result = database.select(new Request[]{
                new Request("value1", new int[]{12}),
                new Request("value2", new int[]{90})
        });

        Assert.assertEquals(Collections.singletonList(new TestIntByteData(12, (byte) 90)), result);
    }

    @Test
    public void shouldSelectByZero() {
        FastSelect<TestIntByteData> database = new FastSelect<>(1, TestIntByteData.class);
        database.addAll(asList(
                new TestIntByteData(12, (byte) 0),
                new TestIntByteData(9, (byte) 91),
                new TestIntByteData(1000, (byte) 89)));

        List result = database.select(new Request[]{
                new Request("value2", new int[]{0})
        });

        Assert.assertEquals(Collections.singletonList(new TestIntByteData(12, (byte) 0)), result);
    }

    @Test
    public void shouldSelectByLongField() {
        FastSelect<LongShortData> database = new FastSelect<>(1, LongShortData.class);
        database.addAll(asList(
                new LongShortData(12L, (short) 0),
                new LongShortData(9, (short) 0),
                new LongShortData(1000, (short) 0)));

        List result = database.select(new Request[]{new Request("long1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new LongShortData(12, (short) 0)), result);
    }

    @Test
    public void shouldSelectByShortField() {
        FastSelect<LongShortData> database = new FastSelect<>(1, LongShortData.class);
        database.addAll(asList(
                new LongShortData(12L, (short) 5),
                new LongShortData(9, (short) 3),
                new LongShortData(1000, (short) 1)));

        List result = database.select(new Request[]{new Request("short1", new int[]{1})});

        Assert.assertEquals(Collections.singletonList(new LongShortData(1000, (short) 1)), result);
    }

    @Test
    public void shouldProvideSize() {
        FastSelect<TestIntByteData> database = new FastSelect<>(1, TestIntByteData.class);
        database.addAll(asList(
                new TestIntByteData(12, (byte) 0),
                new TestIntByteData(9, (byte) 0),
                new TestIntByteData(1000, (byte) 0)));

        Assert.assertEquals(3, database.size());
    }

    @Test
    public void shouldProvideSizeForLong() {
        FastSelect<TestLongData> database = new FastSelect<>(1, TestLongData.class);
        database.addAll(asList(
                new TestLongData(12),
                new TestLongData(9)));

        Assert.assertEquals(2, database.size());
    }

    @Test
    public void supportsLongMultiValuesColumns() {
        FastSelect<LongMultiValuesData> database = new FastSelect<>(1, LongMultiValuesData.class);
        database.addAll(asList(
                new LongMultiValuesData(new long[]{1, 2}),
                new LongMultiValuesData(new long[]{11, 12})));

        List<LongMultiValuesData> r = database.select(new Request[]{new Request("a", new int[]{12})});

        Assert.assertEquals(r, Collections.singletonList(new LongMultiValuesData(new long[]{11, 12})));
    }

    @Test
    public void supportsLongMultiValuesColumnsManyResult() {
        FastSelect<LongMultiValuesData> database = new FastSelect<>(1, LongMultiValuesData.class);
        database.addAll(asList(
                new LongMultiValuesData(new long[]{1, 2}),
                new LongMultiValuesData(new long[]{11, 12}),
                new LongMultiValuesData(new long[]{33, 12}),
                new LongMultiValuesData(new long[]{1, 0})
        ));

        List<LongMultiValuesData> r = database.select(new Request[]{new Request("a", new int[]{12})});

        Assert.assertEquals(asList(new LongMultiValuesData(new long[]{11, 12}), new LongMultiValuesData(new long[]{33, 12})), r);
    }

    @Test
    public void supportsLongMultiValuesColumnsManyCriteria() {
        FastSelect<LongMultiValuesData> database = new FastSelect<>(1, LongMultiValuesData.class);
        database.addAll(asList(
                new LongMultiValuesData(new long[]{1, 2}),
                new LongMultiValuesData(new long[]{11, 12}),
                new LongMultiValuesData(new long[]{33, 12}),
                new LongMultiValuesData(new long[]{1, 0})
        ));

        List<LongMultiValuesData> r = database.select(new Request[]{new Request("a", new int[]{1, 12})});

        Assert.assertEquals(
                asList(
                        new LongMultiValuesData(new long[]{1, 2}),
                        new LongMultiValuesData(new long[]{11, 12}),
                        new LongMultiValuesData(new long[]{33, 12}),
                        new LongMultiValuesData(new long[]{1, 0})),
                r);
    }

    @Test
    public void supportsShortMultiValuesColumns() {
        FastSelect<ShortMultiValuesData> database = new FastSelect<>(1, ShortMultiValuesData.class);
        database.addAll(asList(
                new ShortMultiValuesData(new short[]{1, 2}),
                new ShortMultiValuesData(new short[]{11, 12})));

        List<ShortMultiValuesData> r = database.select(new Request[]{new Request("a", new int[]{12})});

        Assert.assertEquals(r, Collections.singletonList(new ShortMultiValuesData(new short[]{11, 12})));
    }

    @Test
    public void supportsShortMultiValuesColumnsManyResult() {
        FastSelect<ShortMultiValuesData> database = new FastSelect<>(1, ShortMultiValuesData.class);
        database.addAll(asList(
                new ShortMultiValuesData(new short[]{1, 2}),
                new ShortMultiValuesData(new short[]{11, 12}),
                new ShortMultiValuesData(new short[]{33, 12}),
                new ShortMultiValuesData(new short[]{1, 0})
        ));

        List<ShortMultiValuesData> r = database.select(new Request[]{new Request("a", new int[]{12})});

        Assert.assertEquals(asList(new ShortMultiValuesData(new short[]{11, 12}), new ShortMultiValuesData(new short[]{33, 12})), r);
    }

    @Test
    public void supportsShortMultiValuesColumnsManyCriteria() {
        FastSelect<ShortMultiValuesData> database = new FastSelect<>(1, ShortMultiValuesData.class);
        database.addAll(asList(
                new ShortMultiValuesData(new short[]{1, 2}),
                new ShortMultiValuesData(new short[]{11, 12}),
                new ShortMultiValuesData(new short[]{33, 12}),
                new ShortMultiValuesData(new short[]{1, 0})
        ));

        List<ShortMultiValuesData> r = database.select(new Request[]{new Request("a", new int[]{1, 12})});

        Assert.assertEquals(
                asList(
                        new ShortMultiValuesData(new short[]{1, 2}),
                        new ShortMultiValuesData(new short[]{11, 12}),
                        new ShortMultiValuesData(new short[]{33, 12}),
                        new ShortMultiValuesData(new short[]{1, 0})),
                r);
    }

    @Test
    public void supportsByteMultiValuesColumns() {
        FastSelect<ByteMultiValuesData> database = new FastSelect<>(1, ByteMultiValuesData.class);
        database.addAll(asList(
                new ByteMultiValuesData(new byte[]{1, 2}),
                new ByteMultiValuesData(new byte[]{11, 12})));

        List<ByteMultiValuesData> r = database.select(new Request[]{new Request("a", new int[]{12})});

        Assert.assertEquals(r, Collections.singletonList(new ByteMultiValuesData(new byte[]{11, 12})));
    }

    @Test
    public void supportsByteMultiValuesColumnsManyResult() {
        FastSelect<ByteMultiValuesData> database = new FastSelect<>(1, ByteMultiValuesData.class);
        database.addAll(asList(
                new ByteMultiValuesData(new byte[]{1, 2}),
                new ByteMultiValuesData(new byte[]{11, 12}),
                new ByteMultiValuesData(new byte[]{33, 12}),
                new ByteMultiValuesData(new byte[]{1, 0})
        ));

        List<ByteMultiValuesData> r = database.select(new Request[]{new Request("a", new int[]{12})});

        Assert.assertEquals(asList(new ByteMultiValuesData(new byte[]{11, 12}), new ByteMultiValuesData(new byte[]{33, 12})), r);
    }

    @Test
    public void supportsByteMultiValuesColumnsManyCriteria() {
        FastSelect<ByteMultiValuesData> database = new FastSelect<>(1, ByteMultiValuesData.class);
        database.addAll(asList(
                new ByteMultiValuesData(new byte[]{1, 2}),
                new ByteMultiValuesData(new byte[]{11, 12}),
                new ByteMultiValuesData(new byte[]{33, 12}),
                new ByteMultiValuesData(new byte[]{1, 0})
        ));

        List<ByteMultiValuesData> r = database.select(new Request[]{new Request("a", new int[]{1, 12})});

        Assert.assertEquals(
                asList(
                        new ByteMultiValuesData(new byte[]{1, 2}),
                        new ByteMultiValuesData(new byte[]{11, 12}),
                        new ByteMultiValuesData(new byte[]{33, 12}),
                        new ByteMultiValuesData(new byte[]{1, 0})),
                r);
    }

    @Test
    public void shouldCorrectlyRestoreByteField() {
        FastSelect<TestIntByteData> database = new FastSelect<>(10, TestIntByteData.class);
        database.addAll(asList(
                new TestIntByteData(12, (byte) 0),
                new TestIntByteData(12, (byte) -1),
                new TestIntByteData(12, (byte) 1),
                new TestIntByteData(12, Byte.MAX_VALUE),
                new TestIntByteData(12, Byte.MIN_VALUE)));

        List result = database.select(new Request[]{new Request("value1", new int[]{12})});

        Assert.assertEquals(asList(
                new TestIntByteData(12, (byte) 0),
                new TestIntByteData(12, (byte) -1),
                new TestIntByteData(12, (byte) 1),
                new TestIntByteData(12, Byte.MAX_VALUE),
                new TestIntByteData(12, Byte.MIN_VALUE)),
                result);
    }

    @Test
    public void shouldCorrectlyRestoreIntField() {
        FastSelect<TestIntByteData> database = new FastSelect<>(10, TestIntByteData.class);
        database.addAll(asList(
                new TestIntByteData(0, (byte) 1),
                new TestIntByteData(-1, (byte) 1),
                new TestIntByteData(1, (byte) 1),
                new TestIntByteData(Integer.MAX_VALUE, (byte) 1),
                new TestIntByteData(Integer.MIN_VALUE, (byte) 1)));

        List result = database.select(new Request[]{new Request("value2", new int[]{1})});

        Assert.assertEquals(asList(
                new TestIntByteData(0, (byte) 1),
                new TestIntByteData(-1, (byte) 1),
                new TestIntByteData(1, (byte) 1),
                new TestIntByteData(Integer.MAX_VALUE, (byte) 1),
                new TestIntByteData(Integer.MIN_VALUE, (byte) 1)),
                result);
    }

    @Test
    public void shouldCorrectlyRestoreLongField() {
        List<LongShortData> data = asList(
                new LongShortData(0, (short) 1),
                new LongShortData(-1, (short) 1),
                new LongShortData(1, (short) 1),
                new LongShortData(Long.MAX_VALUE, (short) 1),
                new LongShortData(Long.MIN_VALUE, (short) 1));

        FastSelect<LongShortData> database = new FastSelect<>(10, LongShortData.class);
        database.addAll(data);

        List result = database.select(new Request[]{new Request("short1", new int[]{1})});

        Assert.assertEquals(data, result);
    }

}
