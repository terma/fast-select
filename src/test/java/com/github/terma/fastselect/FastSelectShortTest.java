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

package com.github.terma.fastselect;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@SuppressWarnings("WeakerAccess")
public class FastSelectShortTest {

    @Test
    public void shouldSelectAndSortByByteColumn() {
        FastSelect<TestShort> database = new FastSelectBuilder<>(TestShort.class).create();
        database.addAll(asList(
                new TestShort((short) 11),
                new TestShort((short) 4),
                new TestShort((short) 98)));

        Assert.assertEquals(asList(
                new TestShort((short) 4),
                new TestShort((short) 11),
                new TestShort((short) 98)),
                database.selectAndSort(new ColumnRequest[0], "shortValue"));
    }

    @Test
    public void selectByBetween() {
        FastSelect<TestShort> database = new FastSelectBuilder<>(TestShort.class).blockSize(1).create();
        database.addAll(asList(
                new TestShort(Short.MIN_VALUE),
                new TestShort((short) -11),
                new TestShort((short) 0),
                new TestShort((short) 11),
                new TestShort((short) 5),
                new TestShort((short) 4),
                new TestShort(Short.MAX_VALUE)));

        Assert.assertEquals(asList(
                new TestShort(Short.MIN_VALUE),
                new TestShort((short) -11),
                new TestShort((short) 0)),
                database.select(new ShortBetweenRequest("shortValue", Short.MIN_VALUE, (short) 0)));

        Assert.assertEquals(asList(
                new TestShort((short) 5),
                new TestShort((short) 4)),
                database.select(new ShortBetweenRequest("shortValue", (short) 4, (short) 5)));

        Assert.assertEquals(Collections.singletonList(
                new TestShort((short) 4)),
                database.select(new ShortBetweenRequest("shortValue", (short) 4, (short) 4)));

        Assert.assertEquals(Collections.emptyList(),
                database.select(new ShortBetweenRequest("shortValue", (short) 5, (short) 4)));
    }

    @Test
    public void shouldSelectIfManyBlocksOneLevel() {
        FastSelect<TestShort> database = new FastSelectBuilder<>(TestShort.class).blockSize(1).create();
        database.addAll(asList(
                new TestShort((short) 0),
                new TestShort((short) 12),
                new TestShort((short) 0)));

        List result = database.select(new ShortRequest("shortValue", (short) 12));

        Assert.assertEquals(singletonList(new TestShort((short) 12)), result);
    }

    @Test
    public void shouldSupportAddMultipleTimes() {
        FastSelect<TestShort> database = new FastSelectBuilder<>(TestShort.class).blockSize(1).create();
        database.addAll(singletonList(new TestShort(Short.MAX_VALUE)));
        database.addAll(singletonList(new TestShort((short) 0)));
        database.addAll(singletonList(new TestShort(Short.MIN_VALUE)));

        List result = database.select();

        Assert.assertEquals(asList(
                new TestShort(Short.MAX_VALUE),
                new TestShort((short) 0),
                new TestShort(Short.MIN_VALUE)
        ), result);
    }

    @Test
    public void shouldSelectByZero() {
        FastSelect<TestShort> database = new FastSelectBuilder<>(TestShort.class).blockSize(1).create();
        database.addAll(asList(
                new TestShort((short) 0),
                new TestShort((short) 91),
                new TestShort((short) 89)));

        Assert.assertEquals(
                singletonList(new TestShort((short) 0)),
                database.select(new ShortRequest("shortValue", 0)));
    }

    @Test
    public void shouldCorrectlyRestoreField() {
        FastSelect<TestShort> database = new FastSelectBuilder<>(TestShort.class).create();
        database.addAll(asList(
                new TestShort((short) 0),
                new TestShort((short) -1),
                new TestShort((short) 1),
                new TestShort(Short.MAX_VALUE),
                new TestShort(Short.MIN_VALUE)));

        Assert.assertEquals(asList(
                new TestShort((short) 0),
                new TestShort((short) -1),
                new TestShort((short) 1),
                new TestShort(Short.MAX_VALUE),
                new TestShort(Short.MIN_VALUE)),
                database.select());
    }

    public static class TestShort {
        public short shortValue;

        // empty constructor for database to be able restore object
        @SuppressWarnings("unused")
        public TestShort() {
            this((short) 0);
        }

        TestShort(short shortValue) {
            this.shortValue = shortValue;
        }

        @Override
        public String toString() {
            return "TestInt{" +
                    "shortValue=" + shortValue +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestShort testShort = (TestShort) o;

            return shortValue == testShort.shortValue;

        }

        @Override
        public int hashCode() {
            return (int) shortValue;
        }
    }

}
