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
        List result = new ArrayLayoutFastSelect<>(10, TestObject.class, Arrays.asList(
                new ArrayLayoutFastSelect.Column("value1", int.class),
                new ArrayLayoutFastSelect.Column("value2", byte.class)
        ))
                .select(new MultiRequest[]{new MultiRequest("value1", new int[]{34})});
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldSelectIfPresentByOneField() {
        ArrayLayoutFastSelect database = new ArrayLayoutFastSelect<>(10, TestObject.class,
                Arrays.asList(
                        new ArrayLayoutFastSelect.Column("value1", int.class),
                        new ArrayLayoutFastSelect.Column("value2", byte.class)
                ));
        database.addAll(Arrays.asList(
                new TestObject(12, (byte) 0),
                new TestObject(9, (byte) 0),
                new TestObject(1000, (byte) 0)));

        List result = database.select(new MultiRequest[]{new MultiRequest("value1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestObject(12, (byte) 0)), result);
    }

    @Test
    public void shouldSelectIfTwoBlocks() {
        ArrayLayoutFastSelect database = new ArrayLayoutFastSelect<>(1, TestObject.class,
                Arrays.asList(
                        new ArrayLayoutFastSelect.Column("value1", int.class),
                        new ArrayLayoutFastSelect.Column("value2", byte.class)
                ));
        database.addAll(Arrays.asList(
                new TestObject(12, (byte) 0),
                new TestObject(9, (byte) 0),
                new TestObject(1000, (byte) 0)));

        List result = database.select(new MultiRequest[]{new MultiRequest("value1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestObject(12, (byte) 0)), result);
    }

//    @Test
//    public void shouldSelectIfPresentByTwoFields() {
//        ObjectFastSelect<TestObject> database = new ObjectFastSelect<>(
//                TestObject.class, Arrays.asList(
//                new TestObject(11, (byte) 4),
//                new TestObject(11, (byte) 5),
//                new TestObject(12, (byte) 6)
//        ), "value1", "value2");
//
//
//        List result = database.select(new MultiRequest[]{
//                new MultiRequest("value1", new int[]{11}),
//                new MultiRequest("value2", new int[]{4})
//        });
//
//        Assert.assertEquals(Collections.singletonList(new TestObject(11, (byte) 4)), result);
//    }
//
//    @Test
//    public void shouldSelectIfPresentByMultipleValues() {
//        ObjectFastSelect<TestObject> database = new ObjectFastSelect<>(
//                TestObject.class, Arrays.asList(
//                new TestObject(11, (byte) 4),
//                new TestObject(11, (byte) 5),
//                new TestObject(12, (byte) 6)
//        ), "value1", "value2");
//
//        List result = database.select(new MultiRequest[]{
//                new MultiRequest("value2", new int[]{4, 5, 11, 12, 15})
//        });
//
//        Assert.assertEquals(Arrays.asList(new TestObject(11, (byte) 4), new TestObject(11, (byte) 5)), result);
//    }
//
//    @Test
//    public void shouldSelectWhenMoreThanBlockSize() {
//        ObjectFastSelect<TestObject> database = new ObjectFastSelect<>(2,
//                TestObject.class, Arrays.asList(
//                new TestObject(11, (byte) 4),
//                new TestObject(11, (byte) 5),
//                new TestObject(12, (byte) 6),
//                new TestObject(12, (byte) 4)
//        ), "value1", "value2");
//
//        List result = database.select(new MultiRequest[]{
//                new MultiRequest("value2", new int[]{4, 5, 11, 12, 15})
//        });
//
//        Assert.assertEquals(Arrays.asList(
//                new TestObject(11, (byte) 4), new TestObject(11, (byte) 5),
//                new TestObject(12, (byte) 4)), result);
//    }
//
//    @Test
//    public void shouldProvideAccessForItems() {
//        ObjectFastSelect<TestObject> database = new ObjectFastSelect<>(
//                TestObject.class, Arrays.asList(
//                new TestObject(11, (byte) 4),
//                new TestObject(11, (byte) 5)
//        ), "value1", "value2");
//
//
//        Assert.assertEquals(Arrays.asList(new TestObject(11, (byte) 4), new TestObject(11, (byte) 5)), database.getItems());
//    }

    static class TestObject {
        public int value1;
        public byte value2;

        TestObject() {
            this(0, (byte) 0);
        }

        TestObject(int value, byte value2) {
            this.value1 = value;
            this.value2 = value2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return value1 == that.value1 &&
                    value2 == that.value2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value1, value2);
        }
    }

}
