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

public class MultiLongDataTest {

    @Test
    public void provideAllocatedSize() {
        MultiLongData data = new MultiLongData(100);
        Assert.assertEquals(Data.DEFAULT_SIZE, data.allocatedSize());

        for (byte i = 0; i < 50; i++) data.add(new long[]{i});
        Assert.assertEquals(Data.DEFAULT_SIZE + 100, data.allocatedSize());
    }

    @Test
    public void supportCompact() {
        MultiLongData data = new MultiLongData(100);
        for (byte i = 0; i < 17; i++) data.add(new long[]{i});
        Assert.assertEquals(116, data.allocatedSize());

        data.compact();

        Assert.assertEquals(17, data.allocatedSize());
        for (byte i = 0; i < data.size(); i++) Assert.assertArrayEquals(new long[]{i}, (long[]) data.get(i));
    }

    @Test
    public void provideMemSize() {
        MultiLongData data = new MultiLongData(100);
        Assert.assertEquals(280, data.mem());

        for (byte i = 0; i < 50; i++) data.add(new long[] {i});
        Assert.assertEquals(1480, data.mem());
    }

    @Test
    public void provideInc() {
        Assert.assertEquals(33, new MultiLongData(33).inc());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void dontSupportCompare() {
        new MultiLongData(22).compare(0, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void dontSupportPlainCheckAsOldApproach() {
        new MultiLongData(22).plainCheck(0, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void dontSupportCheckAsOldApproach() {
        new MultiLongData(22).check(0, null);
    }

}
