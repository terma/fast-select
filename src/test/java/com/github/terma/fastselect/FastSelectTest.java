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

public class FastSelectTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfRequestedIndexColumnNotExistent() {
        new FastSelect<>(TestObject.class, Collections.emptyList(), "value1", "value2")
                .select(new MultiRequest[]{new MultiRequest("a", new int[]{34})});
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfTryToCreateAndNoRequestedIndexColumnInClass() {
        new FastSelect<>(TestObject.class, Collections.<TestObject>emptyList(), "a");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionIfRequestNull() {
        new FastSelect<>(TestObject.class, Collections.emptyList(), "value1", "value2").select(null);
    }

    @Test
    public void shouldSelectEmptyResultIfNoData() {
        List result = new FastSelect<>(TestObject.class, Collections.emptyList(), "value1", "value2")
                .select(new MultiRequest[]{new MultiRequest("value1", new int[]{34})});
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldSelectIfPresentByOneField() {
        FastSelect<TestObject> database = new FastSelect<>(
                TestObject.class, Arrays.asList(
                new TestObject(12, 0),
                new TestObject(9, 0),
                new TestObject(1000, 0)
        ), "value1", "value2");

        List result = database.select(new MultiRequest[]{new MultiRequest("value1", new int[]{12})});

        Assert.assertEquals(Collections.singletonList(new TestObject(12, 0)), result);
    }

    @Test
    public void shouldSelectIfPresentByTwoFields() {
        FastSelect<TestObject> database = new FastSelect<>(
                TestObject.class, Arrays.asList(
                new TestObject(11, 4),
                new TestObject(11, 5),
                new TestObject(12, 6)
        ), "value1", "value2");


        List result = database.select(new MultiRequest[]{
                new MultiRequest("value1", new int[]{11}),
                new MultiRequest("value2", new int[]{4})
        });

        Assert.assertEquals(Collections.singletonList(new TestObject(11, 4)), result);
    }

    @Test
    public void shouldSelectIfPresentByMultipleValues() {
        FastSelect<TestObject> database = new FastSelect<>(
                TestObject.class, Arrays.asList(
                new TestObject(11, 4),
                new TestObject(11, 5),
                new TestObject(12, 6)
        ), "value1", "value2");

        List result = database.select(new MultiRequest[]{
                new MultiRequest("value2", new int[]{4, 5})
        });

        Assert.assertEquals(Arrays.asList(new TestObject(11, 4), new TestObject(11, 5)), result);
    }

    @Test
    public void shouldProvideAccessForItems() {
        FastSelect<TestObject> database = new FastSelect<>(
                TestObject.class, Arrays.asList(
                new TestObject(11, 4),
                new TestObject(11, 5)
        ), "value1", "value2");


        Assert.assertEquals(Arrays.asList(new TestObject(11, 4), new TestObject(11, 5)), database.getItems());
    }

    static class TestObject {
        public final int value1;
        public final int value2;

        TestObject(int value, int value2) {
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