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

package com.github.terma.fastselect.data;

import org.junit.Assert;
import org.junit.Test;

public class MultiIntDataTest {

    @Test
    public void provideAllocatedSize() {
        MultiIntData data = new MultiIntData(100);
        Assert.assertEquals(Data.DEFAULT_SIZE, data.allocatedSize());

        for (byte i = 0; i < 50; i++) data.add(new int[]{i});
        Assert.assertEquals(Data.DEFAULT_SIZE + 100, data.allocatedSize());
    }

    @Test
    public void initWillResizeDataAndFillByZero() {
        Data data = new MultiIntData(100);
        data.init(100);

        Assert.assertEquals(100, data.size());
        Assert.assertEquals(100, data.allocatedSize());
        for (int i = 0; i < 100; i++) Assert.assertArrayEquals(new int[0], (int[]) data.get(i));
    }

    @Test
    public void initWithZeroIsOk() {
        Data data = new MultiIntData(100);
        data.init(0);

        Assert.assertEquals(0, data.size());
        Assert.assertEquals(0, data.allocatedSize());
    }

    @Test(expected = NegativeArraySizeException.class)
    public void initWithNegativeSizeThrowException() {
        new MultiIntData(100).init(-1);
    }

    @Test
    public void supportCompact() {
        MultiIntData data = new MultiIntData(100);
        for (byte i = 0; i < 17; i++) data.add(new int[]{i});
        Assert.assertEquals(116, data.allocatedSize());

        data.compact();

        Assert.assertEquals(17, data.allocatedSize());
        for (byte i = 0; i < data.size(); i++) Assert.assertArrayEquals(new int[]{i}, (int[]) data.get(i));
    }

    @Test
    public void provideMemSize() {
        MultiIntData data = new MultiIntData(100);
        Assert.assertEquals(216, data.mem());

        for (byte i = 0; i < 50; i++) data.add(new int[] {i});
        Assert.assertEquals(1016, data.mem());
    }

    @Test
    public void provideInc() {
        Assert.assertEquals(33, new MultiIntData(33).inc());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void dontSupportCompare() {
        new MultiIntData(22).compare(0, 1);
    }

}
