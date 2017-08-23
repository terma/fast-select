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

import java.io.IOException;
import java.nio.ByteBuffer;

public class IntDataTest {

    @Test
    public void provideHashCode() {
        IntData data = new IntData(100);
        data.add(12);
        data.add(-90);
        data.add(0);
        data.add(Integer.MAX_VALUE);
        data.add(Integer.MIN_VALUE);
        Assert.assertEquals(12, data.hashCode(0));
        Assert.assertEquals(-90, data.hashCode(1));
        Assert.assertEquals(0, data.hashCode(2));
        Assert.assertEquals(Integer.MAX_VALUE, data.hashCode(3));
        Assert.assertEquals(Integer.MIN_VALUE, data.hashCode(4));
    }

    @Test
    public void provideAllocatedSize() {
        IntData data = new IntData(100);
        Assert.assertEquals(Data.DEFAULT_SIZE, data.allocatedSize());

        for (int i = 0; i < 50; i++) data.add(i);
        Assert.assertEquals(Data.DEFAULT_SIZE + 100, data.allocatedSize());
    }

    @Test
    public void supportCompact() {
        IntData data = new IntData(100);
        for (int i = 0; i < 17; i++) data.add(i);
        Assert.assertEquals(116, data.allocatedSize());

        data.compact();

        Assert.assertEquals(17, data.allocatedSize());
        for (int i = 0; i < data.size(); i++) Assert.assertEquals(i, data.get(i));
    }

    @Test
    public void initWillResizeDataAndFillByZero() {
        Data data = new IntData(100);
        data.init(100);

        Assert.assertEquals(100, data.size());
        Assert.assertEquals(100, data.allocatedSize());
        for (int i = 0; i < 100; i++) Assert.assertEquals(0, data.get(i));
    }

    @Test
    public void initWithZeroIsOk() {
        Data data = new IntData(100);
        data.init(0);

        Assert.assertEquals(0, data.size());
        Assert.assertEquals(0, data.allocatedSize());
    }

    @Test(expected = NegativeArraySizeException.class)
    public void initWithNegativeSizeThrowException() {
        new IntData(100).init(-1);
    }

    @Test
    public void provideMemSize() {
        IntData data = new IntData(100);
        Assert.assertEquals(92, data.mem());

        for (int i = 0; i < 50; i++) data.add(i);
        Assert.assertEquals(492, data.mem());
    }

    @Test
    public void provideInc() {
        Assert.assertEquals(33, new IntData(33).inc());
    }

    @Test
    public void load() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1000);

        IntData data = new IntData(100);
        int[] t = new int[]{0, 1, 2, 3, 4};
        for (int l : t) buffer.putInt(l);

        data.load("", buffer, 5);

        Assert.assertEquals(data.size(), 5);
    }

    @Test
    public void saveAndLoad() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1000);

        IntData data = new IntData(100);
        data.add((byte) -1);
        data.add((byte) 2);
        data.add((byte) 0);
        data.add(Integer.MIN_VALUE);
        data.add(Integer.MAX_VALUE);
        data.save(buffer);

        buffer.flip();
        IntData data1 = new IntData(100);
        data1.load("", buffer, 5);

        Assert.assertEquals(data1.size(), 5);
        Assert.assertEquals(Integer.MIN_VALUE, data1.get(3));
        Assert.assertEquals(Integer.MAX_VALUE, data1.get(4));
    }

}
