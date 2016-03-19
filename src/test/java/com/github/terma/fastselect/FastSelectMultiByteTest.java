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

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;

@SuppressWarnings("WeakerAccess")
public class FastSelectMultiByteTest {

    @Test
    public void selectByNegative() {
        FastSelect<Data> database = new FastSelectBuilder<>(Data.class).blockSize(1).create();
        database.addAll(asList(
                new Data((byte) -1),
                new Data((byte) Byte.MIN_VALUE),
                new Data((byte) -12)));

        assertEquals(singletonList(new Data((byte) -1)),
                database.select(new MultiByteRequest("value", (byte) -1)));

        assertEquals(singletonList(new Data(Byte.MIN_VALUE)),
                database.select(new MultiByteRequest("value", Byte.MIN_VALUE)));
    }

    @Test
    public void selectByZero() {
        FastSelect<Data> database = new FastSelectBuilder<>(Data.class).blockSize(1).create();
        database.addAll(asList(
                new Data((byte) 0),
                new Data((byte) 91),
                new Data((byte) 89)));

        List result = database.select(new MultiByteRequest("value", (byte) 0));

        assertEquals(singletonList(new Data((byte) 0)), result);
    }

    @Test
    public void selectByAge() {
        FastSelect<Data> database = new FastSelectBuilder<>(Data.class).blockSize(1).create();
        database.addAll(asList(
                new Data((byte) Byte.MAX_VALUE),
                new Data((byte) 0),
                new Data((byte) Byte.MIN_VALUE)));

        assertEquals(singletonList(new Data(Byte.MAX_VALUE)),
                database.select(new MultiByteRequest("value", (byte) Byte.MAX_VALUE)));

        assertEquals(singletonList(new Data(Byte.MIN_VALUE)),
                database.select(new MultiByteRequest("value", (byte) Byte.MIN_VALUE)));
    }

    @Test
    public void selectByMultipleValuesAsOr() {
        FastSelect<Data> database = new FastSelectBuilder<>(Data.class).blockSize(1).create();
        database.addAll(asList(
                new Data(new byte[]{1, 2}),
                new Data(new byte[]{11, 12}),
                new Data(new byte[]{33, 12}),
                new Data(new byte[]{Byte.MAX_VALUE, -1}),
                new Data(new byte[]{1, 0})
        ));

        List<Data> r = database.select(
                new MultiByteRequest("value", (byte) 1, (byte) 12, (byte) 2));

        assertEquals(asList(
                new Data((byte) 1, (byte) 2),
                new Data((byte) 11, (byte) 12),
                new Data((byte) 33, (byte) 12),
                new Data((byte) 1, (byte) 0)
        ), r);
    }

    @Test
    public void restore() {
        FastSelect<Data> database = new FastSelectBuilder<>(Data.class).blockSize(1).create();
        database.addAll(asList(
                new Data(new byte[]{Byte.MIN_VALUE, -1, 0}),
                new Data(new byte[]{Byte.MAX_VALUE, 0, 1})));

        List<Data> r = database.select(new MultiByteRequest("value", (byte) 0));

        assertEquals(r, asList(
                new Data(new byte[]{Byte.MIN_VALUE, -1, 0}),
                new Data(new byte[]{Byte.MAX_VALUE, 0, 1})
        ));
    }

    public static class Data {

        public byte[] value;

        @SuppressWarnings("unused")
        public Data() {
        }

        Data(byte... value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "{" + "value=" + Arrays.toString(value) + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data that = (Data) o;
            return Arrays.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(value);
        }
    }

}
