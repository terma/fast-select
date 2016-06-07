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

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@SuppressWarnings("WeakerAccess")
public class FastSelectDoubleTest {

    // todo add support selecting by double

    @Test
    public void supportSortingByDouble() {
        FastSelect<DoubleIntData> database = new FastSelectBuilder<>(DoubleIntData.class).create();
        database.addAll(asList(
                new DoubleIntData(1.0, 11),
                new DoubleIntData(90.12, 4),
                new DoubleIntData(90.00001, 98)));

        List result = database.selectAndSort(new Request[0], "doubleValue");

        Assert.assertEquals(asList(
                new DoubleIntData(1.0, 11),
                new DoubleIntData(90.00001, 98),
                new DoubleIntData(90.12, 4)),
                result);
    }

    @Test
    public void shouldSupportAddMultipleTimes() {
        FastSelect<DoubleIntData> database = new FastSelectBuilder<>(DoubleIntData.class).blockSize(1).create();
        database.addAll(singletonList(new DoubleIntData(-1, 0)));
        database.addAll(singletonList(new DoubleIntData(1, 12)));
        database.addAll(singletonList(new DoubleIntData(0, 0)));

        List result = database.select(new IntRequest("intValue", new int[]{12}));

        Assert.assertEquals(singletonList(new DoubleIntData(1, 12)), result);
    }

    @Test
    public void shouldCorrectlyRestoreDoubleField() {
        FastSelect<DoubleIntData> database = new FastSelectBuilder<>(DoubleIntData.class).create();
        database.addAll(asList(
                new DoubleIntData(Math.PI, 12),
                new DoubleIntData(-1.01333132, 12),
                new DoubleIntData(0, 12),
                new DoubleIntData(Double.MIN_VALUE, 12),
                new DoubleIntData(Double.MAX_VALUE, 12)));

        List result = database.select();

        Assert.assertEquals(asList(
                new DoubleIntData(Math.PI, 12),
                new DoubleIntData(-1.01333132, 12),
                new DoubleIntData(0, 12),
                new DoubleIntData(Double.MIN_VALUE, 12),
                new DoubleIntData(Double.MAX_VALUE, 12)),
                result);
    }

    public static class DoubleIntData {
        public double doubleValue;
        public int intValue;

        @SuppressWarnings("unused")
        public DoubleIntData() {
        }

        public DoubleIntData(double doubleValue, int intValue) {
            this.doubleValue = doubleValue;
            this.intValue = intValue;
        }

        @Override
        public String toString() {
            return "DoubleIntData{" +
                    "doubleValue=" + doubleValue +
                    ", intValue=" + intValue +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DoubleIntData that = (DoubleIntData) o;

            return Double.compare(that.doubleValue, doubleValue) == 0 && intValue == that.intValue;

        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(doubleValue);
            result = (int) (temp ^ (temp >>> 32));
            result = 31 * result + intValue;
            return result;
        }
    }

}
