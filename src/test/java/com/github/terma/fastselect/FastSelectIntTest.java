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
public class FastSelectIntTest {

    @Test
    public void shouldSelectAndSortByColumn() {
        FastSelect<TestInt> database = new FastSelectBuilder<>(TestInt.class).create();
        database.addAll(asList(
                new TestInt(11),
                new TestInt(4),
                new TestInt(98)));

        Assert.assertEquals(asList(
                new TestInt(4),
                new TestInt(11),
                new TestInt(98)),
                database.selectAndSort(new ColumnRequest[0], "intValue"));
    }

    @Test
    public void selectByBetween() {
        FastSelect<TestInt> database = new FastSelectBuilder<>(TestInt.class).blockSize(1).create();
        database.addAll(asList(
                new TestInt(Integer.MIN_VALUE),
                new TestInt(-11),
                new TestInt(0),
                new TestInt(11),
                new TestInt(5),
                new TestInt(4),
                new TestInt(Integer.MAX_VALUE)));

        Assert.assertEquals(asList(
                new TestInt(Integer.MIN_VALUE),
                new TestInt(-11),
                new TestInt(0)),
                database.select(new IntBetweenRequest("intValue", Integer.MIN_VALUE, 0)));

        Assert.assertEquals(asList(
                new TestInt(5),
                new TestInt(4)),
                database.select(new IntBetweenRequest("intValue", 4, 5)));

        Assert.assertEquals(Collections.singletonList(
                new TestInt(4)),
                database.select(new IntBetweenRequest("intValue", 4, 4)));

        Assert.assertEquals(Collections.emptyList(),
                database.select(new IntBetweenRequest("intValue", 5, 4)));
    }

    @Test
    public void shouldSelectIfManyBlocksOneLevel() {
        FastSelect<TestInt> database = new FastSelectBuilder<>(TestInt.class).blockSize(1).create();
        database.addAll(asList(
                new TestInt(0),
                new TestInt(12),
                new TestInt(0)));

        List result = database.select(new IntRequest("intValue", 12));

        Assert.assertEquals(singletonList(new TestInt(12)), result);
    }

    @Test
    public void shouldSupportAddMultipleTimes() {
        FastSelect<TestInt> database = new FastSelectBuilder<>(TestInt.class).blockSize(1).create();
        database.addAll(singletonList(new TestInt(Integer.MAX_VALUE)));
        database.addAll(singletonList(new TestInt(0)));
        database.addAll(singletonList(new TestInt(Integer.MIN_VALUE)));

        List result = database.select();

        Assert.assertEquals(asList(
                new TestInt(Integer.MAX_VALUE),
                new TestInt(0),
                new TestInt(Integer.MIN_VALUE)
        ), result);
    }

    @Test
    public void shouldSelectByZero() {
        FastSelect<TestInt> database = new FastSelectBuilder<>(TestInt.class).blockSize(1).create();
        database.addAll(asList(
                new TestInt(0),
                new TestInt(91),
                new TestInt(89)));

        Assert.assertEquals(
                singletonList(new TestInt(0)),
                database.select(new IntRequest("intValue", 0)));
    }

    @Test
    public void shouldSelectByNot() {
        FastSelect<TestInt> database = new FastSelectBuilder<>(TestInt.class).blockSize(1).create();
        database.addAll(asList(
                new TestInt(0),
                new TestInt(91),
                new TestInt(89)));

        Assert.assertEquals(
                asList(new TestInt(91), new TestInt(89)),
                database.select(new NotRequest(new IntRequest("intValue", 0))));
    }

    @Test
    public void shouldSelectByOr() {
        FastSelect<TestInt> database = new FastSelectBuilder<>(TestInt.class).blockSize(1).create();
        database.addAll(asList(
                new TestInt(0),
                new TestInt(91),
                new TestInt(89)));

        Assert.assertEquals(
                asList(new TestInt(0), new TestInt(89)),
                database.select(new OrRequest(new IntRequest("intValue", 0), new IntRequest("intValue", 89))));
    }

    @Test
    public void shouldCorrectlyRestoreField() {
        FastSelect<TestInt> database = new FastSelectBuilder<>(TestInt.class).create();
        database.addAll(asList(
                new TestInt(0),
                new TestInt(-1),
                new TestInt(1),
                new TestInt(Integer.MAX_VALUE),
                new TestInt(Integer.MIN_VALUE)));

        Assert.assertEquals(asList(
                new TestInt(0),
                new TestInt(-1),
                new TestInt(1),
                new TestInt(Integer.MAX_VALUE),
                new TestInt(Integer.MIN_VALUE)),
                database.select());
    }

    public static class TestInt {
        public int intValue;

        // empty constructor for database to be able restore object
        @SuppressWarnings("unused")
        public TestInt() {
            this(0);
        }

        TestInt(int intValue) {
            this.intValue = intValue;
        }

        @Override
        public String toString() {
            return "TestInt{intValue=" + intValue + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestInt testInt = (TestInt) o;
            return intValue == testInt.intValue;
        }

        @Override
        public int hashCode() {
            return intValue;
        }
    }

}
