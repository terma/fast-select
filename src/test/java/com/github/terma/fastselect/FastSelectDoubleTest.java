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
public class FastSelectDoubleTest {

    @Test
    public void shouldCorrectlyRestoreDoubleField() {
        FastSelect<DoubleData> database = new FastSelectBuilder<>(DoubleData.class).create();
        database.addAll(asList(
                new DoubleData(Math.PI),
                new DoubleData(-1.01333132),
                new DoubleData(0),
                new DoubleData(Double.MIN_VALUE),
                new DoubleData(Double.MAX_VALUE)));

        List result = database.select();

        Assert.assertEquals(asList(
                new DoubleData(Math.PI),
                new DoubleData(-1.01333132),
                new DoubleData(0),
                new DoubleData(Double.MIN_VALUE),
                new DoubleData(Double.MAX_VALUE)),
                result);
    }

    @Test
    public void selectByBetween() {
        FastSelect<DoubleData> database = new FastSelectBuilder<>(DoubleData.class).blockSize(1).create();
        database.addAll(asList(
                new DoubleData(-Double.MAX_VALUE),
                new DoubleData(-11),
                new DoubleData(0),
                new DoubleData(11),
                new DoubleData(5),
                new DoubleData(4),
                new DoubleData(Double.MAX_VALUE)));

        Assert.assertEquals(asList(
                new DoubleData(-Double.MAX_VALUE),
                new DoubleData(-11),
                new DoubleData(0)),
                database.select(new DoubleBetweenRequest("doubleValue", -Double.MAX_VALUE, 0)));

        Assert.assertEquals(asList(
                new DoubleData(5),
                new DoubleData(4)),
                database.select(new DoubleBetweenRequest("doubleValue", 4, 5)));

        Assert.assertEquals(Collections.singletonList(
                new DoubleData(4)),
                database.select(new DoubleBetweenRequest("doubleValue", 4, 4)));

        Assert.assertEquals(Collections.emptyList(),
                database.select(new DoubleBetweenRequest("doubleValue", 5, 4)));
    }

    @Test
    public void supportSortingByDouble() {
        FastSelect<DoubleData> database = new FastSelectBuilder<>(DoubleData.class).create();
        database.addAll(asList(
                new DoubleData(1.0),
                new DoubleData(90.12),
                new DoubleData(90.00001)));

        List result = database.selectAndSort(new Request[0], "doubleValue");

        Assert.assertEquals(asList(
                new DoubleData(1.0),
                new DoubleData(90.00001),
                new DoubleData(90.12)),
                result);
    }

    @Test
    public void shouldSupportAddMultipleTimes() {
        FastSelect<DoubleData> database = new FastSelectBuilder<>(DoubleData.class).blockSize(1).create();
        database.addAll(singletonList(new DoubleData(-1)));
        database.addAll(singletonList(new DoubleData(1)));
        database.addAll(singletonList(new DoubleData(0)));

        List result = database.select();

        Assert.assertEquals(asList(new DoubleData(-1), new DoubleData(1), new DoubleData(0)), result);
    }

    public static class DoubleData {

        public double doubleValue;

        @SuppressWarnings("unused")
        public DoubleData() {
        }

        public DoubleData(double doubleValue) {
            this.doubleValue = doubleValue;
        }

        @Override
        public String toString() {
            return "DoubleData{doubleValue=" + doubleValue + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DoubleData that = (DoubleData) o;

            return Double.compare(that.doubleValue, doubleValue) == 0;

        }

        @Override
        public int hashCode() {
            long temp = Double.doubleToLongBits(doubleValue);
            return (int) (temp ^ (temp >>> 32));
        }
    }

}
