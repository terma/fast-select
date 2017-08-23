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

package com.github.terma.fastselect.data;

import junit.framework.Assert;
import org.junit.Test;

public class DoubleDataTest {

    @Test
    public void provideAllocatedSize() {
        DoubleData data = new DoubleData(100);
        Assert.assertEquals(Data.DEFAULT_SIZE, data.allocatedSize());

        for (long i = 0; i < 50; i++) data.add(i);
        Assert.assertEquals(Data.DEFAULT_SIZE + 100, data.allocatedSize());
    }

    @Test
    public void initWillResizeDataAndFillByZero() {
        DoubleData data = new DoubleData(100);
        data.init(100);

        Assert.assertEquals(100, data.size());
        Assert.assertEquals(100, data.allocatedSize());
        for (int i = 0; i < 100; i++) Assert.assertEquals(0.0, data.get(i));
    }

    @Test
    public void initWithZeroIsOk() {
        DoubleData data = new DoubleData(100);
        data.init(0);

        Assert.assertEquals(0, data.size());
        Assert.assertEquals(0, data.allocatedSize());
    }

    @Test(expected = NegativeArraySizeException.class)
    public void initWithNegativeSizeThrowException() {
        new DoubleData(100).init(-1);
    }

    @Test
    public void supportCompact() {
        DoubleData data = new DoubleData(100);
        for (long i = 0; i < 17; i++) data.add(i);
        Assert.assertEquals(116, data.allocatedSize());

        data.compact();

        Assert.assertEquals(17, data.allocatedSize());
        for (long i = 0; i < data.size(); i++) Assert.assertEquals((double) i, data.get((int) i));
    }

    @Test
    public void supportCompare() {
        DoubleData data = new DoubleData(100);
        data.add(0);
        data.add(-1.1);
        data.add(1.1);
        data.add(Double.MAX_VALUE);
        data.add(Double.MIN_VALUE);

        Assert.assertEquals(0, data.compare(1, 1));
        Assert.assertEquals(1, data.compare(0, 1));
        Assert.assertEquals(-1, data.compare(1, 2));
        Assert.assertEquals(1, data.compare(3, 4));
    }

    @Test
    public void provideMemSize() {
        DoubleData data = new DoubleData(100);
        Assert.assertEquals(156, data.mem());

        for (short i = 0; i < 50; i++) data.add(i);
        Assert.assertEquals(956, data.mem());
    }

    @Test
    public void provideInc() {
        Assert.assertEquals(33, new DoubleData(33).inc());
    }

}
