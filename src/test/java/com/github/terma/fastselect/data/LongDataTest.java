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

public class LongDataTest {

    @Test
    public void provideHashCode() {
        LongData data = new LongData(100);
        data.add(12);
        data.add(-90);
        data.add(0);
        data.add(Long.MAX_VALUE);
        data.add(Long.MIN_VALUE);
        data.add(Long.MIN_VALUE + 10);
        Assert.assertEquals(12, data.hashCode(0));
        Assert.assertEquals(89, data.hashCode(1));
        Assert.assertEquals(0, data.hashCode(2));
        Assert.assertEquals(-2147483648, data.hashCode(3));
        Assert.assertEquals(-2147483648, data.hashCode(4));
        Assert.assertEquals(-2147483638, data.hashCode(5));
    }

    @Test
    public void provideAllocatedSize() {
        LongData data = new LongData(100);
        Assert.assertEquals(Data.DEFAULT_SIZE, data.allocatedSize());

        for (long i = 0; i < 50; i++) data.add(i);
        Assert.assertEquals(Data.DEFAULT_SIZE + 100, data.allocatedSize());
    }

    @Test
    public void initWillResizeDataAndFillByZero() {
        LongData data = new LongData(100);
        data.init(100);

        Assert.assertEquals(100, data.size());
        Assert.assertEquals(100, data.allocatedSize());
        for (int i = 0; i < 100; i++) Assert.assertEquals((long) 0, data.get(i));
    }

    @Test
    public void initWithZeroIsOk() {
        LongData data = new LongData(100);
        data.init(0);

        Assert.assertEquals(0, data.size());
        Assert.assertEquals(0, data.allocatedSize());
    }

    @Test(expected = NegativeArraySizeException.class)
    public void initWithNegativeSizeThrowException() {
        new LongData(100).init(-1);
    }

    @Test
    public void supportCompact() {
        LongData data = new LongData(100);
        for (long i = 0; i < 17; i++) data.add(i);
        Assert.assertEquals(116, data.allocatedSize());

        data.compact();

        Assert.assertEquals(17, data.allocatedSize());
        for (long i = 0; i < data.size(); i++) Assert.assertEquals(i, data.get((int) i));
    }

    @Test
    public void provideMemSize() {
        LongData data = new LongData(100);
        Assert.assertEquals(156, data.mem());

        for (short i = 0; i < 50; i++) data.add(i);
        Assert.assertEquals(956, data.mem());
    }

    @Test
    public void provideInc() {
        Assert.assertEquals(33, new LongData(33).inc());
    }

    @Test
    public void load() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10000);

        LongData data = new LongData(100);
        long[] t = new long[]{0, 1, 2, 3, 4};
        for (long l : t) byteBuffer.putLong(l);

        data.load("", byteBuffer, 5);

        Assert.assertEquals(data.size(), 5);
    }

    @Test
    public void saveAndLoad() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10000);

        LongData data = new LongData(100);
        data.add((byte) -1);
        data.add((byte) 2);
        data.add((byte) 0);
        data.add(Long.MIN_VALUE);
        data.add(Long.MAX_VALUE);
        data.save(byteBuffer);

        byteBuffer.flip();
        LongData data1 = new LongData(100);
        data1.load("", byteBuffer, 5);

        Assert.assertEquals(data1.size(), 5);
        Assert.assertEquals(Long.MIN_VALUE, data1.get(3));
        Assert.assertEquals(Long.MAX_VALUE, data1.get(4));
    }

}
