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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

public class ShortDataTest {

    @Test
    public void provideAllocatedSize() {
        ShortData data = new ShortData(100);
        Assert.assertEquals(Data.DEFAULT_SIZE, data.allocatedSize());

        for (short i = 0; i < 50; i++) data.add(i);
        Assert.assertEquals(Data.DEFAULT_SIZE + 100, data.allocatedSize());
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
        ShortData data = new ShortData(100);
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        short[] t = new short[]{0, 1, 2, 3, 4};
        ByteBuffer b = fc.map(FileChannel.MapMode.READ_WRITE, 0, Data.SHORT_BYTES * t.length);
        for (short l : t) b.putShort(l);
        fc.force(true);

        data.load(fc, 5);
        f.delete();

        Assert.assertEquals(data.size(), 5);
    }

}
