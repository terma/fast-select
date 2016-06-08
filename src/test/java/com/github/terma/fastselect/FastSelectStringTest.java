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
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@SuppressWarnings("WeakerAccess")
public class FastSelectStringTest {

    @Test
    public void shouldSelectAndSortByColumn() {
        FastSelect<TestString> database = new FastSelectBuilder<>(TestString.class).create();
        database.addAll(asList(
                new TestString("11"),
                new TestString("4"),
                new TestString("12"),
                new TestString("98")));

        Assert.assertEquals(asList(
                new TestString("11"),
                new TestString("12"),
                new TestString("4"),
                new TestString("98")),
                database.selectAndSort(new Request[0], "stringValue"));
    }

    @Test
    public void shouldSelectIfManyBlocksOneLevel() {
        FastSelect<TestString> database = new FastSelectBuilder<>(TestString.class).blockSize(1).create();
        database.addAll(asList(
                new TestString("0"),
                new TestString("12"),
                new TestString("0")));

        List result = database.select(new StringRequest("stringValue", "12"));

        Assert.assertEquals(singletonList(new TestString("12")), result);
    }

    @Test
    public void shouldSupportAddMultipleTimes() {
        FastSelect<TestString> database = new FastSelectBuilder<>(TestString.class).blockSize(1).create();
        database.addAll(singletonList(new TestString("Short.MAX_VALUE")));
        database.addAll(singletonList(new TestString("0")));
        database.addAll(singletonList(new TestString("Short.MIN_VALUE")));

        List result = database.select();

        Assert.assertEquals(asList(
                new TestString("Short.MAX_VALUE"),
                new TestString("0"),
                new TestString("Short.MIN_VALUE")
        ), result);
    }

    @Test
    public void shouldSelectByString() {
        FastSelect<TestString> database = new FastSelectBuilder<>(TestString.class).blockSize(1).create();
        database.addAll(asList(
                new TestString("A"),
                new TestString("ARA"),
                new TestString("ARAR")));

        Assert.assertEquals(
                singletonList(new TestString("ARA")),
                database.select(new StringRequest("stringValue", "ARA")));
    }

    @Test
    public void shouldSelectByInsensitiveString() {
        FastSelect<TestString> database = new FastSelectBuilder<>(TestString.class).blockSize(1).create();
        database.addAll(asList(
                new TestString("A"),
                new TestString("aRa"),
                new TestString("aRaR")));

        Assert.assertEquals(
                Collections.singletonList(new TestString("aRaR")),
                database.select(new StringNoCaseLikeRequest("stringValue", "ARAR")));
    }

    @Test
    public void shouldSelectByLike() {
        FastSelect<TestString> database = new FastSelectBuilder<>(TestString.class).blockSize(1).create();
        database.addAll(asList(
                new TestString("A"),
                new TestString("aRa"),
                new TestString("aRaR")));

        Assert.assertEquals(
                asList(new TestString("aRa"), new TestString("aRaR")),
                database.select(new StringLikeRequest("stringValue", "R")));

        Assert.assertEquals(
                asList(new TestString("aRa"), new TestString("aRaR")),
                database.select(new StringLikeRequest("stringValue", "aR")));

        Assert.assertEquals(
                Collections.singletonList(new TestString("aRaR")),
                database.select(new StringLikeRequest("stringValue", "RaR")));

        Assert.assertEquals(
                Collections.emptyList(),
                database.select(new StringLikeRequest("stringValue", "ara")));

        Assert.assertEquals(
                asList(new TestString("A"), new TestString("aRa"), new TestString("aRaR")),
                database.select(new StringLikeRequest("stringValue", "")));

        Assert.assertEquals(
                Collections.singletonList(new TestString("aRaR")),
                database.select(new StringLikeRequest("stringValue", "aRaR")));
    }

    @Test
    public void shouldSelectByMultiple() {
        FastSelect<TestString> database = new FastSelectBuilder<>(TestString.class).blockSize(1).create();
        database.addAll(asList(
                new TestString("A"),
                new TestString("B"),
                new TestString("Cucumber")));

        Assert.assertEquals(
                Collections.emptyList(),
                database.select(new StringMultipleRequest("stringValue", "")));

        Assert.assertEquals(
                Collections.emptyList(),
                database.select(new StringMultipleRequest("stringValue", "nono", "nea")));

        Assert.assertEquals(
                Collections.singletonList(new TestString("A")),
                database.select(new StringMultipleRequest("stringValue", "A")));

        Assert.assertEquals(
                asList(new TestString("A"), new TestString("Cucumber")),
                database.select(new StringMultipleRequest("stringValue", "A", "Cucumber")));
    }

    @Test
    public void shouldSelectByEmptyString() {
        FastSelect<TestString> database = new FastSelectBuilder<>(TestString.class).blockSize(1).create();
        database.addAll(asList(
                new TestString("0"),
                new TestString(""),
                new TestString("89")));

        Assert.assertEquals(
                singletonList(new TestString("")),
                database.select(new StringRequest("stringValue", "")));
    }

    @Test
    public void shouldCorrectlyRestoreField() {
        FastSelect<TestString> database = new FastSelectBuilder<>(TestString.class).create();
        database.addAll(asList(
                new TestString("0"),
                new TestString("-1"),
                new TestString("1")));

        Assert.assertEquals(asList(
                new TestString("0"),
                new TestString("-1"),
                new TestString("1")),
                database.select());
    }

    public static class TestString {
        public String stringValue;

        // empty constructor for database to be able restore object
        @SuppressWarnings("unused")
        public TestString() {
            this("");
        }

        TestString(String stringValue) {
            this.stringValue = stringValue;
        }

        @Override
        public String toString() {
            return "TestString{" +
                    "stringValue=" + stringValue +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestString TestString = (TestString) o;

            return Objects.equals(stringValue, TestString.stringValue);

        }

        @Override
        public int hashCode() {
            return stringValue.hashCode();
        }
    }

}
