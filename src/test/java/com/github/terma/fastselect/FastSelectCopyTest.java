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

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class FastSelectCopyTest {

    @Test
    public void whenCopyEmptyShouldCreateNewInstanceWithSameConfigurationAndNoData() {
        FastSelect<ScalarData> fastSelect = new FastSelectBuilder<>(ScalarData.class).create();

        FastSelect<ScalarData> copy = fastSelect.copy(new Request[]{new IntRequest("intValue", 0)});

        Assert.assertNotSame(fastSelect, copy);
        Assert.assertEquals(0, copy.size());
        Assert.assertEquals(fastSelect.getColumns().size(), copy.getColumns().size());
        Assert.assertEquals(fastSelect.dataBlockSize(), copy.dataBlockSize());
        Assert.assertEquals(fastSelect.allocatedSize(), copy.allocatedSize());
    }

    @Test
    public void whenCopyShouldCopyScalarDataAsWell() {
        FastSelect<ScalarData> fastSelect = new FastSelectBuilder<>(ScalarData.class).blockSize(2).create();

        ScalarData data1 = new ScalarData();
        data1.byteValue = 45;
        data1.shortValue = (short) 19000;
        data1.intValue = 678990;
        data1.longValue = Long.MAX_VALUE / 2;

        ScalarData data2 = new ScalarData();
        data2.byteValue = 45;
        data2.shortValue = (short) 19000;
        data2.intValue = 678990;
        data2.longValue = Long.MAX_VALUE / 2;

        ScalarData data3 = new ScalarData();
        data3.byteValue = 1;
        data3.shortValue = (short) 45;
        data3.intValue = 900000;
        data3.longValue = -1;

        List<ScalarData> data = Arrays.asList(data1, data2, data3);
        fastSelect.addAll(data);

        FastSelect<ScalarData> copy = fastSelect.copy(new Request[]{});

        Assert.assertNotSame(fastSelect, copy);
        Assert.assertEquals(fastSelect.size(), copy.size());
        Assert.assertEquals(data, copy.select());

        Request[] intWhere = new Request[]{new IntRequest("intValue", data2.intValue)};
        Assert.assertEquals(fastSelect.select(intWhere), copy.select(intWhere));

        Request[] byteWhere = new Request[]{new ByteRequest("byteValue", data2.byteValue)};
        Assert.assertEquals(fastSelect.select(byteWhere), copy.select(byteWhere));
    }

    @Test
    public void shouldCopyArrayTypes() {
        FastSelect<ArrayData> fastSelect = new FastSelectBuilder<>(ArrayData.class).blockSize(2).create();

        ArrayData data1 = new ArrayData();
        data1.byteValue = new byte[]{1, 90, -100};
        data1.shortValue = new short[]{(short) 19000, (short) -19000};
        data1.intValue = new int[]{1, 70, 100, 9000000};
        data1.longValue = new long[]{Long.MAX_VALUE / 2, Long.MIN_VALUE};

        ArrayData data2 = new ArrayData();
        data2.byteValue = new byte[0];
        data2.shortValue = new short[0];
        data2.intValue = new int[0];
        data2.longValue = new long[0];

        ArrayData data3 = new ArrayData();
        data3.byteValue = new byte[1000];
        data3.byteValue[900] = 12;
        data3.shortValue = new short[90];
        data3.shortValue[89] = -999;
        data3.intValue = new int[100];
        data3.longValue = new long[1000];
        data3.longValue[999] = Long.MIN_VALUE;

        List<ArrayData> data = Arrays.asList(data1, data2, data3);
        fastSelect.addAll(data);

        FastSelect<ArrayData> copy = fastSelect.copy(new Request[]{});

        Assert.assertNotSame(fastSelect, copy);
        Assert.assertEquals(fastSelect.size(), copy.size());
        Assert.assertEquals(data, copy.select());

        Request[] intWhere = new Request[]{new MultiIntRequest("intValue", 70)};
        Assert.assertEquals(fastSelect.select(intWhere), copy.select(intWhere));

        Request[] byteWhere = new Request[]{new MultiByteRequest("byteValue", (byte) 0)};
        Assert.assertEquals(fastSelect.select(byteWhere), copy.select(byteWhere));
    }

    @Test
    public void shouldCopyStringType() {
        FastSelect<StringData> fastSelect = new FastSelectBuilder<>(StringData.class).blockSize(2).create();

        StringData data1 = new StringData();
        data1.stringValue = "";

        StringData data2 = new StringData();
        data2.stringValue = "just string";

        StringData data3 = new StringData();
        data3.stringValue = "Long text with ,12312312314 ";

        List<StringData> data = Arrays.asList(data1, data2, data3);
        fastSelect.addAll(data);

        FastSelect<StringData> copy = fastSelect.copy(new Request[]{});

        Assert.assertNotSame(fastSelect, copy);
        Assert.assertEquals(data, copy.select());
    }

    public static class ArrayData {
        public byte[] byteValue;
        public short[] shortValue;
        public int[] intValue;
        public long[] longValue;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ArrayData arrayData = (ArrayData) o;

            if (!Arrays.equals(byteValue, arrayData.byteValue)) return false;
            if (!Arrays.equals(shortValue, arrayData.shortValue)) return false;
            if (!Arrays.equals(intValue, arrayData.intValue)) return false;
            return Arrays.equals(longValue, arrayData.longValue);

        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(byteValue);
            result = 31 * result + Arrays.hashCode(shortValue);
            result = 31 * result + Arrays.hashCode(intValue);
            result = 31 * result + Arrays.hashCode(longValue);
            return result;
        }
    }

    public static class StringData {
        public String stringValue;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StringData that = (StringData) o;

            return stringValue != null ? stringValue.equals(that.stringValue) : that.stringValue == null;

        }

        @Override
        public int hashCode() {
            return stringValue != null ? stringValue.hashCode() : 0;
        }
    }

    public static class ScalarData {
        public byte byteValue;
        public short shortValue;
        public int intValue;
        public long longValue;

        @Override
        public String toString() {
            return "ScalarData{" +
                    "byteValue=" + byteValue +
                    ", shortValue=" + shortValue +
                    ", intValue=" + intValue +
                    ", longValue=" + longValue +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ScalarData that = (ScalarData) o;

            if (byteValue != that.byteValue) return false;
            if (shortValue != that.shortValue) return false;
            if (intValue != that.intValue) return false;
            return longValue == that.longValue;

        }

        @Override
        public int hashCode() {
            int result = (int) byteValue;
            result = 31 * result + (int) shortValue;
            result = 31 * result + intValue;
            result = 31 * result + (int) (longValue ^ (longValue >>> 32));
            return result;
        }
    }

}
