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

public class StringCompressedShortDataTest {

    @Test
    public void initWillResizeDataAndFillByZero() {
        Data data = new StringCompressedShortData(100);
        data.init(100);

        Assert.assertEquals(100, data.size());
        Assert.assertEquals(100, data.allocatedSize());
        for (int i = 0; i < 100; i++) Assert.assertNull(data.get(i));
    }

    @Test
    public void initWithZeroIsOk() {
        Data data = new StringCompressedShortData(100);
        data.init(0);

        Assert.assertEquals(0, data.size());
        Assert.assertEquals(0, data.allocatedSize());
    }

    @Test(expected = NegativeArraySizeException.class)
    public void initWithNegativeSizeThrowException() {
        new StringCompressedShortData(100).init(-1);
    }

    @Test
    public void saveLoad() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);

        StringCompressedShortData data = new StringCompressedShortData(100);
        data.add("ABACA");
        data.add("XY");
        data.add("");
        data.add(null);
        data.save(buffer);
        buffer.flip();

        StringCompressedShortData data1 = new StringCompressedShortData(100);
        data1.load("", buffer, 4);

        Assert.assertEquals(data1.size(), 4);
        Assert.assertEquals("ABACA", data1.get(0));
        Assert.assertEquals("XY", data1.get(1));
        Assert.assertEquals("", data1.get(2));
        Assert.assertEquals(null, data1.get(3));
    }

    @Test
    public void saveLoadNonAscIICharacters() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);

        StringCompressedShortData data = new StringCompressedShortData(100);
        data.add("侍");
        data.save(buffer);
        buffer.flip();

        StringCompressedShortData data1 = new StringCompressedShortData(100);
        data1.load("", buffer, 1);

        Assert.assertEquals(data1.size(), 1);
        Assert.assertEquals("侍", data1.get(0));
    }

}
