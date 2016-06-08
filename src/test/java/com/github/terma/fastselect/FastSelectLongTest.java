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

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@SuppressWarnings("WeakerAccess")
public class FastSelectLongTest {

    @Test
    public void shouldSelectAndSortByColumn() {
        FastSelect<TestLong> database = new FastSelectBuilder<>(TestLong.class).create();
        database.addAll(asList(
                new TestLong(11),
                new TestLong(4),
                new TestLong(98)));

        Assert.assertEquals(asList(
                new TestLong(4),
                new TestLong(11),
                new TestLong(98)),
                database.selectAndSort(new Request[0], "longValue"));
    }

    @Test
    public void selectByBetween() {
        FastSelect<TestLong> database = new FastSelectBuilder<>(TestLong.class).blockSize(1).create();
        database.addAll(asList(
                new TestLong(Long.MIN_VALUE),
                new TestLong(-11),
                new TestLong(0),
                new TestLong(11),
                new TestLong(5),
                new TestLong(4),
                new TestLong(Long.MAX_VALUE)));

        Assert.assertEquals(asList(
                new TestLong(Long.MIN_VALUE),
                new TestLong(-11),
                new TestLong(0)),
                database.select(new LongBetweenRequest("longValue", Long.MIN_VALUE, 0)));

        Assert.assertEquals(asList(
                new TestLong(5),
                new TestLong(4)),
                database.select(new LongBetweenRequest("longValue", 4, 5)));

        Assert.assertEquals(Collections.singletonList(
                new TestLong(4)),
                database.select(new LongBetweenRequest("longValue", 4, 4)));

        Assert.assertEquals(Collections.emptyList(),
                database.select(new LongBetweenRequest("longValue", 5, 4)));
    }

    @Test
    public void shouldSelectIfManyBlocksOneLevel() {
        FastSelect<TestLong> database = new FastSelectBuilder<>(TestLong.class).blockSize(1).create();
        database.addAll(asList(
                new TestLong(0),
                new TestLong(12),
                new TestLong(0)));

        List result = database.select(new LongRequest("longValue", 12));

        Assert.assertEquals(singletonList(new TestLong(12)), result);
    }

    @Test
    public void shouldSupportAddMultipleTimes() {
        FastSelect<TestLong> database = new FastSelectBuilder<>(TestLong.class).blockSize(1).create();
        database.addAll(singletonList(new TestLong(Long.MAX_VALUE)));
        database.addAll(singletonList(new TestLong(0)));
        database.addAll(singletonList(new TestLong(Long.MIN_VALUE)));

        List result = database.select();

        Assert.assertEquals(asList(
                new TestLong(Long.MAX_VALUE),
                new TestLong(0),
                new TestLong(Long.MIN_VALUE)
        ), result);
    }

    @Test
    public void shouldSelectByZero() {
        FastSelect<TestLong> database = new FastSelectBuilder<>(TestLong.class).blockSize(1).create();
        database.addAll(asList(
                new TestLong(0),
                new TestLong(91),
                new TestLong(89)));

        Assert.assertEquals(
                singletonList(new TestLong(0)),
                database.select(new LongRequest("longValue", 0)));
    }

    @Test
    public void shouldCorrectlyRestoreField() {
        FastSelect<TestLong> database = new FastSelectBuilder<>(TestLong.class).create();
        database.addAll(asList(
                new TestLong(0),
                new TestLong(-1),
                new TestLong(1),
                new TestLong(Long.MAX_VALUE),
                new TestLong(Long.MIN_VALUE)));

        Assert.assertEquals(asList(
                new TestLong(0),
                new TestLong(-1),
                new TestLong(1),
                new TestLong(Long.MAX_VALUE),
                new TestLong(Long.MIN_VALUE)),
                database.select());
    }

    public static class TestLong {
        public long longValue;

        // empty constructor for database to be able restore object
        @SuppressWarnings("unused")
        public TestLong() {
            this(0);
        }

        TestLong(long longValue) {
            this.longValue = longValue;
        }

        @Override
        public String toString() {
            return "TestLong{" +
                    "longValue=" + longValue +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestLong TestLong = (TestLong) o;

            return longValue == TestLong.longValue;

        }

        @Override
        public int hashCode() {
            return (int) longValue;
        }
    }

}
