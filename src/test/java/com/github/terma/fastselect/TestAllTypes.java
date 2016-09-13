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

import com.github.terma.fastselect.data.StringCompressedByte;
import com.github.terma.fastselect.data.StringCompressedInt;
import com.github.terma.fastselect.data.StringCompressedShort;

import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class TestAllTypes {
    public byte byteValue;
    public short shortValue;
    public int intValue;
    public long longValue;
    public double doubleValue;
    public byte[] multiByteValue = new byte[0];
    public short[] multiShortValue = new short[0];
    public int[] multiIntValue = new int[0];
    public long[] multiLongValue = new long[0];
    public String stringValue = "";
    @StringCompressedByte
    public String stringCompressedByteValue;
    @StringCompressedShort
    public String stringCompressedShortValue;
    @StringCompressedInt
    public String stringCompressedIntValue;

    @Override
    public String toString() {
        return "TestAllTypes{" +
                "byteValue=" + byteValue +
                ", shortValue=" + shortValue +
                ", intValue=" + intValue +
                ", longValue=" + longValue +
                ", doubleValue=" + doubleValue +
                ", multiByteValue=" + Arrays.toString(multiByteValue) +
                ", multiShortValue=" + Arrays.toString(multiShortValue) +
                ", multiIntValue=" + Arrays.toString(multiIntValue) +
                ", multiLongValue=" + Arrays.toString(multiLongValue) +
                ", stringValue=" + stringValue +
                ", stringCompressedByteValue='" + stringCompressedByteValue + '\'' +
                ", stringCompressedShortValue='" + stringCompressedShortValue + '\'' +
                ", stringCompressedIntValue='" + stringCompressedIntValue + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestAllTypes that = (TestAllTypes) o;

        if (byteValue != that.byteValue) return false;
        if (shortValue != that.shortValue) return false;
        if (intValue != that.intValue) return false;
        if (longValue != that.longValue) return false;
        if (Double.compare(that.doubleValue, doubleValue) != 0) return false;
        if (!Arrays.equals(multiByteValue, that.multiByteValue)) return false;
        if (!Arrays.equals(multiShortValue, that.multiShortValue)) return false;
        if (!Arrays.equals(multiIntValue, that.multiIntValue)) return false;
        if (!Arrays.equals(multiLongValue, that.multiLongValue)) return false;
        if (stringValue != null ? !stringValue.equals(that.stringValue) : that.stringValue != null) return false;
        if (stringCompressedByteValue != null ? !stringCompressedByteValue.equals(that.stringCompressedByteValue) : that.stringCompressedByteValue != null)
            return false;
        if (stringCompressedShortValue != null ? !stringCompressedShortValue.equals(that.stringCompressedShortValue) : that.stringCompressedShortValue != null)
            return false;
        return stringCompressedIntValue != null ? stringCompressedIntValue.equals(that.stringCompressedIntValue) : that.stringCompressedIntValue == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) byteValue;
        result = 31 * result + (int) shortValue;
        result = 31 * result + intValue;
        result = 31 * result + (int) (longValue ^ (longValue >>> 32));
        temp = Double.doubleToLongBits(doubleValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + Arrays.hashCode(multiByteValue);
        result = 31 * result + Arrays.hashCode(multiShortValue);
        result = 31 * result + Arrays.hashCode(multiIntValue);
        result = 31 * result + Arrays.hashCode(multiLongValue);
        result = 31 * result + (stringValue != null ? stringValue.hashCode() : 0);
        result = 31 * result + (stringCompressedByteValue != null ? stringCompressedByteValue.hashCode() : 0);
        result = 31 * result + (stringCompressedShortValue != null ? stringCompressedShortValue.hashCode() : 0);
        result = 31 * result + (stringCompressedIntValue != null ? stringCompressedIntValue.hashCode() : 0);
        return result;
    }

    public TestAllTypes andLongValue(long value) {
        this.longValue = value;
        return this;
    }
}
