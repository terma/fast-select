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

public class ShortDataTest {

    @Test
    public void provideHashCode() {
        ShortData data = new ShortData(100);
        data.add((short)12);
        data.add((short)-90);
        data.add((short)0);
        data.add(Short.MAX_VALUE);
        data.add(Short.MIN_VALUE);
        Assert.assertEquals(12, data.hashCode(0));
        Assert.assertEquals(-90, data.hashCode(1));
        Assert.assertEquals(0, data.hashCode(2));
        Assert.assertEquals(32767, data.hashCode(3));
        Assert.assertEquals(-32768, data.hashCode(4));
    }

    @Test
    public void provideAllocatedSize() {
        ShortData data = new ShortData(100);
        Assert.assertEquals(Data.DEFAULT_SIZE, data.allocatedSize());

        for (short i = 0; i < 50; i++) data.add(i);
        Assert.assertEquals(Data.DEFAULT_SIZE + 100, data.allocatedSize());
    }

    @Test
    public void initWillResizeDataAndFillByZero() {
        Data data = new ShortData(100);
        data.init(100);

        Assert.assertEquals(100, data.size());
        Assert.assertEquals(100, data.allocatedSize());
        for (int i = 0; i < 100; i++) Assert.assertEquals((short) 0, data.get(i));
    }

    @Test
    public void initWithZeroIsOk() {
        Data data = new ShortData(100);
        data.init(0);

        Assert.assertEquals(0, data.size());
        Assert.assertEquals(0, data.allocatedSize());
    }

    @Test(expected = NegativeArraySizeException.class)
    public void initWithNegativeSizeThrowException() {
        new ShortData(100).init(-1);
    }

    @Test
    public void supportCompact() {
        ShortData data = new ShortData(100);
        for (short i = 0; i < 17; i++) data.add(i);
        Assert.assertEquals(116, data.allocatedSize());

        data.compact();

        Assert.assertEquals(17, data.allocatedSize());
        for (short i = 0; i < data.size(); i++) Assert.assertEquals(i, data.get(i));
    }

    @Test
    public void provideMemSize() {
        ShortData data = new ShortData(100);
        Assert.assertEquals(60, data.mem());

        for (short i = 0; i < 50; i++) data.add(i);
        Assert.assertEquals(260, data.mem());
    }

    @Test
    public void provideInc() {
        Assert.assertEquals(33, new ShortData(33).inc());
    }

    @Test
    public void load() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        ShortData data = new ShortData(100);
        short[] t = new short[]{0, 1, 2, 3, 4};
        for (short l : t) buffer.putShort(l);

        data.load("", buffer, 5);

        Assert.assertEquals(data.size(), 5);
    }

    @Test
    public void saveAndLoad() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1000);

        ShortData data = new ShortData(100);
        data.add((byte) -1);
        data.add((byte) 2);
        data.add((byte) 0);
        data.add(Short.MIN_VALUE);
        data.add(Short.MAX_VALUE);
        data.save(buffer);

        buffer.flip();
        ShortData data1 = new ShortData(100);
        data1.load("", buffer, 5);

        Assert.assertEquals(data1.size(), 5);
        Assert.assertEquals(Short.MIN_VALUE, data1.get(3));
        Assert.assertEquals(Short.MAX_VALUE, data1.get(4));
    }

}
