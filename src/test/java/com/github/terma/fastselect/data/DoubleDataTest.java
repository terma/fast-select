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
